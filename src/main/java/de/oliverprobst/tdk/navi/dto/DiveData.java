package de.oliverprobst.tdk.navi.dto;

import de.oliverprobst.tdk.navi.controller.DiveDataProperties;

public class DiveData extends AbstractModel implements Cloneable {

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
		clone.inclination = inclination;
		clone.humidity = humidity;
		clone.freeText = freeText;
		clone.voltage = voltage;
		return clone;
	}

	public DiveData() {
		super();
	}

	private float depth;
	private long divetime;
	private long surfacetime;
	private int course;
	private String gga;
	private int runtimeScooter;
	private int timeSinceLastGPS;
	private float temperature;
	private int inclination;
	private int freeText;
	private float humidity;
	private int voltage;

	/**
	 * @return the surfacetime
	 */
	public long getSurfacetime() {
		return surfacetime;
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
	 * @return the depth
	 */
	public float getDepth() {
		return depth;
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
	 * @return the divetime
	 */
	public long getDivetime() {
		return divetime;
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
	 * @return the course
	 */
	public int getCourse() {
		return course;
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
	 * @return the lastNMEA
	 */
	public String getGga() {
		return gga;
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
	 * @return the runtimeScooter
	 */
	public int getRuntimeScooter() {
		return runtimeScooter;
	}

	/**
	 * @param runtimeScooter
	 *            the runtimeScooter to set
	 */
	public void setRuntimeScooter(int runtimeScooter) {
		this.runtimeScooter = runtimeScooter;
	}

	/**
	 * @return the timeSinceLastGPS
	 */
	public int getTimeSinceLastGPS() {
		return timeSinceLastGPS;
	}

	/**
	 * @param timeSinceLastGPS
	 *            the timeSinceLastGPS to set
	 */
	public void setTimeSinceLastGPS(int timeSinceLastGPS) {
		this.timeSinceLastGPS = timeSinceLastGPS;
	}

	/**
	 * @return the temperature
	 */
	public float getTemperature() {
		return temperature;
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
	 * @return the inclination
	 */
	public int getInclination() {
		return inclination;
	}

	/**
	 * @param inclination
	 *            the inclination to set
	 */
	public void setInclination(int inclination) {
		this.inclination = inclination;
	}

	/**
	 * @return the freeText
	 */
	public int getFreeText() {
		return freeText;
	}

	/**
	 * @param freeText
	 *            the freeText to set
	 */
	public void setFreeText(int freeText) {
		this.freeText = freeText;
	}

	/**
	 * @return the humidity
	 */
	public float getHumidity() {
		return humidity;
	}

	/**
	 * @param humidity
	 *            the humidity to set
	 */
	public void setHumidity(float humidity) {
		this.humidity = humidity;
	}

	/**
	 * @return the voltage
	 */
	public int getVoltage() {
		return voltage;
	}

	/**
	 * @param voltage
	 *            the voltage to set
	 */
	public void setVoltage(int voltage) {
		this.voltage = voltage;
	}

}
