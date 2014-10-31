package de.oliverprobst.tdk.navi.controller;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oliverprobst.tdk.navi.App;
import de.oliverprobst.tdk.navi.GeoCalculator;
import de.oliverprobst.tdk.navi.LocationEstimator;
import de.oliverprobst.tdk.navi.NmeaParser;
import de.oliverprobst.tdk.navi.config.Waypoint;
import de.oliverprobst.tdk.navi.dto.DiveData;
import de.oliverprobst.tdk.navi.dto.Location;
import de.oliverprobst.tdk.navi.dto.PitchAndCourse;
import de.oliverprobst.tdk.navi.dto.StructuralIntegrity;
import de.oliverprobst.tdk.navi.dto.StructuralIntegrity.Status;

public class DefaultController {

	private final List<DiveData> record = new LinkedList<DiveData>();

	/**
	 * @return the currentRecord
	 */
	public DiveData getCurrentRecord() {
		return currentRecord;
	}

	private final Collection<Waypoint> wps = new LinkedList<Waypoint>();

	private String notes = "";

	private String mapImage = "";
	private boolean brightTheme = false;

	/**
	 * @return the brightTheme
	 */
	public boolean isBrightTheme() {
		return brightTheme;
	}

	/**
	 * @param brightTheme
	 *            the brightTheme to set
	 */
	public void setBrightTheme(boolean brightTheme) {
		this.brightTheme = brightTheme;
	}

	private long lastGPSfix = 0;
	private long lastPosEstimation = -1;

	/**
	 * @return the mapImage
	 */
	public String getMapImage() {
		return mapImage;
	}

	/**
	 * @param mapImage
	 *            the mapImage to set
	 */
	public void setMapImage(String mapImage) {
		this.mapImage = mapImage;
	}

	private DiveData currentRecord = new DiveData();

	private Logger log = LoggerFactory.getLogger(DefaultController.class);

	/**
	 * Set to true when the first time below 1.5m
	 */
	private boolean isDiving = false;

	/**
	 * Timestamp of first time below 1.5m
	 */
	private long diveStartTimestamp = 0;

	/**
	 * Timer if diver is up again: First timestamp above 1m depth.
	 */
	private long onSurfaceSinceTimestamp = 0;

	/**
	 * Overall time on surface after the dive started.
	 */
	private long overallSurfaceTime = 0;

	private final StructuralIntegrityController structuralIntegrityController = new StructuralIntegrityController();

	protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);

	public DefaultController(final int logIntervall) {

		// used also to set the divetime and clock!
		Thread recordThread = new Thread() {

			int count = 0;
			int errcount = 0;

			@Override
			public void run() {
				super.run();
				while (true) {
					if (isDiving) {
						record.add(getCurrentRecordClone());
						if (count++ > 1) {
							count = 0;
							updateDiveProfile();
						}
					}
					try {
						Thread.sleep(logIntervall);
					} catch (InterruptedException e) {
						log.error("Thread sleep interrupted!", e);
						if (errcount++ > 50) {
							// the eject button
							log.error("Stopped profile update and dive data recording thread due to serious interupt problems.");
							break;
						}
					}
				}
			}

		};
		recordThread.start();
	}

	protected void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue,
				newValue);
	}

	/**
	 * @return the currentRecord
	 */
	public DiveData getCurrentRecordClone() {
		return currentRecord.clone();
	}

	/**
	 * @return the notes
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * O
	 * 
	 * @return the record
	 */
	public List<DiveData> getRecord() {
		return record;
	}

	public Collection<Waypoint> getWPs() {
		return wps;
	}

	public void registerControllerPropertyChangeListener(
			PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void registerModelPropertyListener(PropertyChangeListener pcl) {
		currentRecord.addPropertyChangeListener(pcl);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/**
	 * @param depth
	 * @see de.oliverprobst.tdk.navi.dto.DiveData#setDepth(float)
	 */
	public void setDepth(float depth) {
		// set dive timer:
		if (depth > 1.5f && !isDiving) {
			// first time below 1.5m
			isDiving = true;
			diveStartTimestamp = System.currentTimeMillis();

		} else if (depth <= 1.0f && isDiving && onSurfaceSinceTimestamp == 0) {
			// first time below 1m after a decent
			onSurfaceSinceTimestamp = System.currentTimeMillis();

		} else if (depth <= 1.0f && isDiving && onSurfaceSinceTimestamp != 0) {
			// being on surface after a decent for a while
			currentRecord.setSurfacetime(overallSurfaceTime
					+ (System.currentTimeMillis() - onSurfaceSinceTimestamp));

		} else if (depth > 1.5f && isDiving && onSurfaceSinceTimestamp != 0) {
			// first time below 1m after a surface interval
			overallSurfaceTime += System.currentTimeMillis()
					- onSurfaceSinceTimestamp;
			onSurfaceSinceTimestamp = 0;

		} else if (isDiving) {
			// diver down
			currentRecord
					.setDivetime((System.currentTimeMillis() - diveStartTimestamp)
							- overallSurfaceTime);
		}

		currentRecord.setDepth(depth);
	}

	public void setGGA(String message) {
		lastPosEstimation = -1;
		lastGPSfix = System.currentTimeMillis();
		currentRecord.setGga(message);
	}

	/**
	 * @param humidity
	 * @see de.oliverprobst.tdk.navi.dto.DiveData#setHumidity(float)
	 */
	public void setHumidity(int humidity) {
		currentRecord.setHumidity(humidity);
	}

	/**
	 * @param integrity
	 * @see de.oliverprobst.tdk.navi.dto.DiveData#setIntegrity(de.oliverprobst.tdk.navi.dto.StructuralIntegrity)
	 */
	public void setIntegrityCode(String integrityCode) {
		StructuralIntegrity si = structuralIntegrityController.construct(
				integrityCode, currentRecord.getDepth());
		if (si != null) {
			currentRecord.setIntegrity(si);
			if (si.getBow() == Status.BROKEN || si.getStern() == Status.BROKEN) {
				this.shutdown("leak");
			}
		}
	}

	/**
	 * @param notes
	 *            the notes to set
	 */
	public void setNotes(String notes) {
		firePropertyChange(DiveDataProperties.PROP_NOTES, this.notes, notes);
		this.notes = notes;
	}

	public void setGear(int speedByVibration) {
		double gear = LocationEstimator.getInstance().calcScooterGear(
				speedByVibration);
		estimateLocation();
		currentRecord.setGear(gear);
	}

	/**
	 * @param temperature
	 * @see de.oliverprobst.tdk.navi.dto.DiveData#setTemperature(float)
	 */
	public void setTemperature(float temperature) {
		currentRecord.setTemperature(temperature);

	}

	/**
	 * @param f
	 * @see de.oliverprobst.tdk.navi.dto.DiveData#setVoltage(int)
	 */
	public void setVoltage(float f) {
		currentRecord.setVoltage(f);
		if (f < App.getConfig().getSettings().getShutdownVoltage()) {
			this.shutdown("low voltage");
		}
	}

	private void updateDiveProfile() {
		firePropertyChange(DiveDataProperties.PROP_UPDATEPROFILE, null, record);
	}

	public void estimateLocation() {

		// first try 20 sec after last fix or 10 seconds after last estimate
		if (System.currentTimeMillis() - lastGPSfix < 5000
				|| System.currentTimeMillis() - lastPosEstimation < 5000) {
			return;
		}

		// only if pitch is inside tolerance
		final int pitchTolerance = 10;

		if (currentRecord.getPitchAndCourse() == null) {
			return;
		}

		if (currentRecord.getPitchAndCourse().getFrontRearPitch() < pitchTolerance
				* -1
				&& currentRecord.getPitchAndCourse().getFrontRearPitch() > pitchTolerance
				&& currentRecord.getPitchAndCourse().getLeftRightPitch() < pitchTolerance
						* -1
				&& currentRecord.getPitchAndCourse().getLeftRightPitch() > pitchTolerance) {
			return;
		}

		// no speed, no new location...
		if (currentRecord.getGear() == 0) {
			return;
		}

		if (currentRecord.getGga() == null) {
			return;
		}

		// determine last position (preferable by GPS)
		double latitude = 0;
		double longitude = 0;
		long timeSinceLastLoc = System.currentTimeMillis() - lastPosEstimation;
		int heading = currentRecord.getPitchAndCourse().getCourse();
		Location lastEstimation = currentRecord.getEstimatedLocation();
		if (lastPosEstimation == -1) {
			NmeaParser parser = new NmeaParser(currentRecord.getGga());
			latitude = parser.getLatitude();
			longitude = parser.getLongitude();
			timeSinceLastLoc = System.currentTimeMillis() - this.lastGPSfix;
		} else {
			latitude = lastEstimation.getLatitude();
			longitude = lastEstimation.getLongitude();
		}

		// let's do the new estimation:
		lastPosEstimation = System.currentTimeMillis();
		double distance = (((double) timeSinceLastLoc / 60000d) * LocationEstimator
				.getInstance().calcScooterSpeed(currentRecord.getGear()));
		Location estimatedLocation = GeoCalculator.getInstance()
				.calculateNewLocation(latitude, longitude, heading, distance);

		currentRecord.setEstimatedLocation(estimatedLocation);

	}

	/**
	 * Send a new pitch and course measurement to the current data set.
	 * 
	 * @param pAc
	 *            Measured pitch and course
	 */
	public void setPitchAndCourse(PitchAndCourse pAc) {
		estimateLocation();
		currentRecord.setPitchAndCourse(pAc);
	}

	/**
	 * Shutdown received
	 * 
	 * @param payload
	 *            The reason for the shut down. If reported via serial, payload
	 *            is '1'. if payload == 'leak', shutdown will be faster.
	 */
	public void shutdown(String payload) {
		currentRecord.shutdown(payload);
		if (payload.equals("1")) {
			payload = "User command";
		}
		log.info("Shutdown initiated. Reason: " + payload);

		int waitForUser = 5000;
		if (payload.equals("leak")
				&& App.getConfig().getSettings().isFastLeakShutdown()) {
			waitForUser = 100;
		}

		Thread shutdownThread = new ShutdownThread(waitForUser);
		shutdownThread.start();

	}
}
