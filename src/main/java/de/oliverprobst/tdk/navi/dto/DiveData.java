package de.oliverprobst.tdk.navi.dto;

import de.oliverprobst.tdk.navi.controller.DiveDataProperties;

public class DiveData extends AbstractModel implements Cloneable {

	private int course;

	private float depth;

	private long divetime;
	private int freeText;
	private int frontRearPitch = 0;
	private String gga;
	private int humidity;
	private StructuralIntegrity integrity = new StructuralIntegrity();
	private int leftRightPitch = 0;
	private String pitch;
	private int runtimeScooter;
	private int speed;
	private long surfacetime;
	private float temperature;
	private int timeSinceLastGPS;
	private float voltage;
	private Location estimatedLocation = new Location();

	/**
	 * @return the estimatedLocation
	 */
	public Location getEstimatedLocation() {
		return estimatedLocation;
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

	public DiveData() {
		super();
	}

	public DiveData clone() {
		DiveData clone = new DiveData();
		clone.setTimestamp(getTimestamp());
		clone.depth = depth;
		clone.divetime = divetime;
		clone.surfacetime = surfacetime;
		clone.course = course;
		clone.gga = gga;
		clone.runtimeScooter = runtimeScooter;
		clone.timeSinceLastGPS = timeSinceLastGPS;
		clone.temperature = temperature;
		clone.pitch = pitch;
		clone.humidity = humidity;
		clone.freeText = freeText;
		clone.voltage = voltage;
		clone.speed = speed;
		clone.leftRightPitch = leftRightPitch;
		clone.frontRearPitch = frontRearPitch;
		clone.integrity = integrity.clone();
		clone.estimatedLocation = estimatedLocation.clone();
		return clone;
	}

	/**
	 * @return the course
	 */
	public int getCourse() {
		return course;
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
	 * @return the freeText
	 */
	public int getFreeText() {
		return freeText;
	}

	/**
	 * @return the frontRearPitch
	 */
	public int getFrontRearPitch() {
		return frontRearPitch;
	}

	/**
	 * @return the lastNMEA
	 */
	public String getGga() {
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
	 * @return the leftRightPitch
	 */
	public int getLeftRightPitch() {
		return leftRightPitch;
	}

	/**
	 * @return the pitch
	 */
	public String getPitch() {
		return pitch;
	}

	/**
	 * @return the runtimeScooter
	 */
	public int getRuntimeScooter() {
		return runtimeScooter;
	}

	/**
	 * @return the speed
	 */
	public int getSpeed() {
		return speed;
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
	 * @param course
	 *            the course to set
	 */
	public void setCourse(int course) {
		super.propertyChangeSupport.firePropertyChange(
				DiveDataProperties.PROP_COURSE, this.course, course);
		this.course = course;
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
	 * @param freeText
	 *            the freeText to set
	 */
	public void setFreeText(int freeText) {
		this.freeText = freeText;
	}

	/**
	 * @param gga
	 *            the lastNMEA to set
	 */
	public void setGga(String gga) {
		super.propertyChangeSupport.firePropertyChange(
				DiveDataProperties.PROP_GPSFIX, this.gga, gga);
		this.gga = gga;
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

	/**
	 * @param pitch
	 *            the pitch to set
	 */
	public void setPitch(String pitch) {
		super.propertyChangeSupport.firePropertyChange(
				DiveDataProperties.PROP_PITCH, this.pitch, pitch);
		this.pitch = pitch;
		String[] values = pitch.split(",");
		frontRearPitch = Integer.parseInt(values[0]);
		leftRightPitch = Integer.parseInt(values[1]);
	}

	/**
	 * @param runtimeScooter
	 *            the runtimeScooter to set
	 */
	public void setRuntimeScooter(int runtimeScooter) {
		this.runtimeScooter = runtimeScooter;
	}

	/**
	 * @param speed
	 *            the speed to set
	 */
	public void setSpeed(int speed) {
		super.propertyChangeSupport.firePropertyChange(
				DiveDataProperties.PROP_SPEED, this.speed, speed);
		this.speed = speed;
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

}
