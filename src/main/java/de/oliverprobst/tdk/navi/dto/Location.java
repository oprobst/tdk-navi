package de.oliverprobst.tdk.navi.dto;

public class Location implements Cloneable {

	public Location() {

	}

	public Location(double latitude, double longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
	}

	private long timestamp = System.currentTimeMillis();
	private double latitude;
	private double longitude;

	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLocation(double latitude, double longitude) {
		timestamp = System.currentTimeMillis();
		this.longitude = longitude;
		this.latitude = latitude;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	public Location clone() {
		Location clone = new Location();
		clone.timestamp = timestamp;
		clone.latitude = latitude;
		clone.longitude = longitude;
		return clone;
	}
}
