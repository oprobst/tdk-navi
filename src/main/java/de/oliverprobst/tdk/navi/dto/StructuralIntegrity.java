package de.oliverprobst.tdk.navi.dto;

public class StructuralIntegrity implements Cloneable {

	public enum Status {
		BROKEN, OK, PROBLEMATIC
	}

	private Status abient = Status.OK;
	private Status bow = Status.OK;
	private int lastCode = 0;
	private int pressure = 1013;

	private Status stern = Status.OK;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof StructuralIntegrity)) {
			return false;
		}
		StructuralIntegrity other = (StructuralIntegrity) obj;
		if (lastCode != other.lastCode) {
			return false;
		}
		return true;
	}

	/**
	 * @return the abient
	 */
	public Status getAmbient() {
		return abient;
	}

	/**
	 * @return the bow
	 */
	public Status getBow() {
		return bow;
	}

	/**
	 * @return the lastCode
	 */
	public int getLastCode() {
		return lastCode;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + lastCode;
		return result;
	}

	/**
	 * @param abient
	 *            the abient to set
	 */
	public void setAmbient(Status abient) {
		this.abient = abient;
	}

	/**
	 * @param bow
	 *            the bow to set
	 */
	public void setBow(Status bow) {
		this.bow = bow;
	}

	/**
	 * @param lastCode
	 *            the lastCode to set
	 */
	public void setLastCode(int lastCode) {
		this.lastCode = lastCode;
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
}
