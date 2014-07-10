package de.oliverprobst.tdk.navi.dto;

public class StructuralIntegrity implements Cloneable {

	private int pressure = 1013;
	private Status stern = Status.OK;
	private Status bow = Status.OK;
	private Status abient = Status.OK;
	private int lastCode = 0;

	/**
	 * @return the lastCode
	 */
	public int getLastCode() {
		return lastCode;
	}

	/**
	 * @param lastCode
	 *            the lastCode to set
	 */
	public void setLastCode(int lastCode) {
		this.lastCode = lastCode;
	}

	public StructuralIntegrity() {

	}

	public StructuralIntegrity clone() {
		StructuralIntegrity clone = new StructuralIntegrity();
		clone.lastCode = lastCode;
		clone.pressure = pressure;
		clone.stern = stern;
		clone.bow = bow;
		clone.abient = abient;
		return clone;
	}

	/**
	 * @return the pressure
	 */
	public int getPressure() {
		return pressure;
	}

	/**
	 * @param pressure
	 *            the pressure to set
	 */
	public void setPressure(int pressure) {
		this.pressure = pressure;
	}

	/**
	 * @return the stern
	 */
	public Status getStern() {
		return stern;
	}

	/**
	 * @param stern
	 *            the stern to set
	 */
	public void setStern(Status stern) {
		this.stern = stern;
	}

	/**
	 * @return the bow
	 */
	public Status getBow() {
		return bow;
	}

	/**
	 * @param bow
	 *            the bow to set
	 */
	public void setBow(Status bow) {
		this.bow = bow;
	}

	/**
	 * @return the abient
	 */
	public Status getAmbient() {
		return abient;
	}

	/**
	 * @param abient
	 *            the abient to set
	 */
	public void setAmbient(Status abient) {
		this.abient = abient;
	}

	public enum Status {
		OK, PROBLEMATIC, BROKEN
	}
}
