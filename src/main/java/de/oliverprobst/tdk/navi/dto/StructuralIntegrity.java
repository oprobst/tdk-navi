package de.oliverprobst.tdk.navi.dto;

public class StructuralIntegrity implements Cloneable {

	public enum Status {
		BROKEN, OK, PROBLEMATIC
	}

	private Status ambient = Status.OK;
	private Status bow = Status.OK;
	private int bowSensorValue = -1;

	private int pressure = 1013;

	private Status stern = Status.OK;

	private int sternSensorValue = -1;

	public StructuralIntegrity() {

	}

	public StructuralIntegrity clone() {
		StructuralIntegrity clone = new StructuralIntegrity();
		clone.pressure = pressure;
		clone.stern = stern;
		clone.bow = bow;
		clone.ambient = ambient;
		return clone;
	}

	/**
	 * @return the abient
	 */
	public Status getAmbient() {
		return ambient;
	}

	/**
	 * @return the bow
	 */
	public Status getBow() {
		return bow;
	}

	/**
	 * @return the bowSensorValue
	 */
	public int getBowSensorValue() {
		return bowSensorValue;
	}

	/**
	 * @return the pressure
	 */
	public int getPressure() {
		return pressure;
	}

	/**
	 * @return the stern
	 */
	public Status getStern() {
		return stern;
	}

	/**
	 * @return the sternSensorValue
	 */
	public int getSternSensorValue() {
		return sternSensorValue;
	}

	/**
	 * @param abient
	 *            the abient to set
	 */
	public void setAmbient(Status abient) {
		this.ambient = abient;
	}

	/**
	 * @param bow
	 *            the bow to set
	 */
	public void setBow(Status bow) {
		this.bow = bow;
	}

	/**
	 * @param bowSensorValue
	 *            the bowSensorValue to set
	 */
	public void setBowSensorValue(int bowSensorValue) {
		this.bowSensorValue = bowSensorValue;
	}

	/**
	 * @param pressure
	 *            the pressure to set
	 */
	public void setPressure(int pressure) {
		this.pressure = pressure;
	}

	/**
	 * @param stern
	 *            the stern to set
	 */
	public void setStern(Status stern) {
		this.stern = stern;
	}

	/**
	 * @param sternSensorValue
	 *            the sternSensorValue to set
	 */
	public void setSternSensorValue(int sternSensorValue) {
		this.sternSensorValue = sternSensorValue;
	}

}
