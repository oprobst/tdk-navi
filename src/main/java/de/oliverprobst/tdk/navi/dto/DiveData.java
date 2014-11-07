package de.oliverprobst.tdk.navi.dto;

import de.oliverprobst.tdk.navi.NmeaParser;
import de.oliverprobst.tdk.navi.controller.DiveDataProperties;

public class DiveData extends AbstractModel implements Cloneable {

	private float depth;

	private long divetime;
	private Location estimatedLocation = new Location();
	private int freeText;
	private double gear;
	private NmeaParser gga;
	private int humidity;
	private StructuralIntegrity integrity = new StructuralIntegrity();
	private PitchAndCourse pitchAndCourse = new PitchAndCourse(0, 0, 0);

	private int runtimeScooter;

	private long surfacetime;
	private float temperature;
	private int timeSinceLastGPS;
	private float voltage;

	public DiveData() {
		super();
	}

	public DiveData clone() {
		DiveData clone = new DiveData();
		clone.setTimestamp(getTimestamp());
		clone.depth = depth;
		clone.divetime = divetime;
		clone.surfacetime = surfacetime;
		clone.gga = gga;
		clone.runtimeScooter = runtimeScooter;
		clone.timeSinceLastGPS = timeSinceLastGPS;
		clone.temperature = temperature;
		clone.humidity = humidity;
		clone.freeText = freeText;
		clone.voltage = voltage;
		clone.gear = gear;
		clone.pitchAndCourse = pitchAndCourse.clone();
		clone.integrity = integrity.clone();
		clone.estimatedLocation = estimatedLocation.clone();
		return clone;
	}

	/**
	 * @return the depth
	 */
	public float getDepth() {
		return depth;
	}

	/**
	 * @return the divetime
	 */
	public long getDivetime() {
		return divetime;
	}

	/**
	 * @return the estimatedLocation
	 */
	public Location getEstimatedLocation() {
		return estimatedLocation;
	}

	/**
	 * @return the freeText
	 */
	public int getFreeText() {
		return freeText;
	}

	/**
	 * @return the gear
	 */
	public double getGear() {
		return gear;
	}

	/**
	 * @return the lastNMEA
	 */
	public NmeaParser getGga() {
		return gga;
	}

	/**
	 * @return the humidity
	 */
	public int getHumidity() {
		return humidity;
	}

	/**
	 * @return the integrity
	 */
	public StructuralIntegrity getIntegrity() {
		return integrity;
	}

	/**
	 * @return the pitchAndCourse
	 */
	public PitchAndCourse getPitchAndCourse() {
		return pitchAndCourse;
	}

	/**
	 * @return the runtimeScooter
	 */
	public int getRuntimeScooter() {
		return runtimeScooter;
	}

	/**
	 * @return the surfacetime
	 */
	public long getSurfacetime() {
		return surfacetime;
	}

	/**
	 * @return the temperature
	 */
	public float getTemperature() {
		return temperature;
	}

	/**
	 * @return the timeSinceLastGPS
	 */
	public int getTimeSinceLastGPS() {
		return timeSinceLastGPS;
	}

	/**
	 * @return the voltage
	 */
	public float getVoltage() {
		return voltage;
	}

	/**
	 * @param depth
	 *            the depth to set
	 */
	public void setDepth(float depth) {
		super.propertyChangeSupport.firePropertyChange(
				DiveDataProperties.PROP_DEPTH, this.depth, depth);
		this.depth = depth;
	}

	/**
	 * @param divetime
	 *            the divetime to set
	 */
	public void setDivetime(long divetime) {
		super.propertyChangeSupport.firePropertyChange(
				DiveDataProperties.PROP_DIVETIME, this.divetime, divetime);
		this.divetime = divetime;
	}

	/**
	 * @param estimatedLocation
	 *            the estimatedLocation to set
	 */
	public void setEstimatedLocation(Location estimatedLocation) {
		super.propertyChangeSupport.firePropertyChange(
				DiveDataProperties.PROP_ESTIMATED, this.estimatedLocation,
				estimatedLocation);
		this.estimatedLocation = estimatedLocation;
	}

	/**
	 * @param freeText
	 *            the freeText to set
	 */
	public void setFreeText(int freeText) {
		this.freeText = freeText;
	}

	/**
	 * @param speed
	 *            the speed to set
	 */
	public void setGear(double gear) {
		super.propertyChangeSupport.firePropertyChange(
				DiveDataProperties.PROP_SPEED, this.gear, gear);
		this.gear = gear;
	}

	/**
	 * @param nmeaMsg
	 *            the lastNMEA to set
	 */
	public void setGga(NmeaParser nmeaMsg) {
		super.propertyChangeSupport.firePropertyChange(
				DiveDataProperties.PROP_GPSFIX, this.gga, nmeaMsg);
		this.gga = nmeaMsg;
	}

	/**
	 * @param humidity
	 *            the humidity to set
	 */
	public void setHumidity(int humidity) {
		super.propertyChangeSupport.firePropertyChange(
				DiveDataProperties.PROP_HUMIDITY, this.humidity, humidity);
		this.humidity = humidity;
	}

	/**
	 * @param integrity
	 *            the integrity to set
	 */
	public void setIntegrity(StructuralIntegrity integrity) {
		super.propertyChangeSupport.firePropertyChange(
				DiveDataProperties.PROP_HULL, this.integrity, integrity);
		this.integrity = integrity;
	}

	public void setPitchAndCourse(PitchAndCourse pAc) {
		super.propertyChangeSupport.firePropertyChange(
				DiveDataProperties.PROP_COURSE,
				this.pitchAndCourse.getCourse(), pAc.getCourse());

		super.propertyChangeSupport.firePropertyChange(
				DiveDataProperties.PROP_PITCH, this.pitchAndCourse, pAc);
		this.pitchAndCourse = pAc;
	}

	/**
	 * @param runtimeScooter
	 *            the runtimeScooter to set
	 */
	public void setRuntimeScooter(int runtimeScooter) {
		this.runtimeScooter = runtimeScooter;
	}

	/**
	 * @param surfacetime
	 *            the surfacetime to set
	 */
	public void setSurfacetime(long surfacetime) {
		super.propertyChangeSupport.firePropertyChange(
				DiveDataProperties.PROP_SURFACETIME, this.surfacetime,
				surfacetime);
		this.surfacetime = surfacetime;
	}

	/**
	 * @param temperature
	 *            the temperature to set
	 */
	public void setTemperature(float temperature) {
		super.propertyChangeSupport.firePropertyChange(
				DiveDataProperties.PROP_TEMPERATUR, this.temperature,
				temperature);
		this.temperature = temperature;
	}

	/**
	 * @param timeSinceLastGPS
	 *            the timeSinceLastGPS to set
	 */
	public void setTimeSinceLastGPS(int timeSinceLastGPS) {
		this.timeSinceLastGPS = timeSinceLastGPS;
	}

	/**
	 * @param voltage
	 *            the voltage to set
	 */
	public void setVoltage(float voltage) {
		super.propertyChangeSupport.firePropertyChange(
				DiveDataProperties.PROP_VOLTAGE, this.voltage, voltage);
		this.voltage = voltage;
	}

	public void shutdown(String payload) {
		super.propertyChangeSupport.firePropertyChange(
				DiveDataProperties.PROP_SHUTDOWN, "", payload);
	}

	/**
	 * Return all values as comma separated String.
	 *
	 * @return All values of the Dive Data.
	 */
	public String toCommaSeparatedString() {
		StringBuilder sb = new StringBuilder();

		sb.append(getTimestamp());
		sb.append(",");
		sb.append(depth);
		sb.append(",");
		sb.append(divetime);
		sb.append(",");
		sb.append(surfacetime);
		sb.append(",");
		sb.append(gga.getLatitude());
		sb.append(",");
		sb.append(gga.getLongitude());
		sb.append(",");
		sb.append(gga.getSatelliteCount());
		sb.append(",");
		sb.append(gga.getSignalQuality());
		sb.append(",");
		sb.append(gga.getClock());
		sb.append(",");
		sb.append(runtimeScooter);
		sb.append(",");
		sb.append(timeSinceLastGPS);
		sb.append(",");
		sb.append(temperature);
		sb.append(",");
		sb.append(humidity);
		sb.append(",");
		sb.append(voltage);
		sb.append(",");
		sb.append(gear);
		sb.append(",");
		sb.append(pitchAndCourse.getCourse());
		sb.append(",");
		sb.append(pitchAndCourse.getFrontRearPitch());
		sb.append(",");
		sb.append(pitchAndCourse.getLeftRightPitch());
		sb.append(",");
		sb.append(integrity.getSternSensorValue());
		sb.append(",");
		sb.append(integrity.getBowSensorValue());
		sb.append(",");
		sb.append(integrity.getPressure());
		sb.append(",");
		sb.append(estimatedLocation.getLatitude());
		sb.append(",");
		sb.append(estimatedLocation.getLongitude());
		sb.append("\n");
		return sb.toString();

	}
}
