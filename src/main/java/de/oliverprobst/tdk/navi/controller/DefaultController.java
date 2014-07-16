package de.oliverprobst.tdk.navi.controller;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oliverprobst.tdk.navi.config.Waypoint;
import de.oliverprobst.tdk.navi.dto.DiveData;
import de.oliverprobst.tdk.navi.dto.StructuralIntegrity;

public class DefaultController {

	private final List<DiveData> record = new LinkedList<DiveData>();

	private final Collection<Waypoint> wps = new LinkedList<Waypoint>();

	private String notes = "";

	private String mapImage = "";
	
	/**
	 * @return the mapImage
	 */
	public String getMapImage() {
		return mapImage;
	}

	/**
	 * @param mapImage the mapImage to set
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

	public DefaultController() {

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
						Thread.sleep(1000);
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
	 * @param course
	 * @see de.oliverprobst.tdk.navi.dto.DiveData#setCourse(int)
	 */
	public void setCourse(int course) {
		currentRecord.setCourse(course);
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
	public void setIntegrityCode(int integrityCode) {
		StructuralIntegrity si = structuralIntegrityController.parseCode(
				integrityCode, currentRecord.getDepth());
		currentRecord.setIntegrity(si);
	}

	/**
	 * @param notes
	 *            the notes to set
	 */
	public void setNotes(String notes) {
		firePropertyChange(DiveDataProperties.PROP_NOTES, this.notes, notes);
		this.notes = notes;
	}

	/**
	 * @param inclination
	 * @see de.oliverprobst.tdk.navi.dto.DiveData#setInclination(int)
	 */
	public void setPitch(int pitch) {
		currentRecord.setPitch(pitch);
	}

	public void setSpeed(int speed) {
		currentRecord.setSpeed(speed);
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
	}

	private void updateDiveProfile() {
		firePropertyChange(DiveDataProperties.PROP_UPDATEPROFILE, null, record);
	}

}