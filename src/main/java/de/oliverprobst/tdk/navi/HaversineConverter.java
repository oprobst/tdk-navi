package de.oliverprobst.tdk.navi;

import java.awt.Dimension;
import java.awt.Point;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HaversineConverter {

	private Logger log = LoggerFactory.getLogger(App.class);

	public final static double AVERAGE_RADIUS_OF_EARTH = 6371;

	/**
	 * @return the averageRadiusOfEarth
	 */
	public static double getAverageRadiusOfEarth() {
		return AVERAGE_RADIUS_OF_EARTH;
	}

	private static HaversineConverter instance = new HaversineConverter();

	private double nwCornerLat = 0;

	private double nwCornerLng = 0;

	private double seCornerLat = 0;

	private double seCornerLng = 0;

	private double swCornerLat = 0;

	private double swCornerLng = 0;

	private HaversineConverter() {

	}

	/**
	 * @param fromLat
	 * @param fromLng
	 * @param toLat
	 * @param toLng
	 * @return distance in meter
	 */
	public int calculateDistance(double fromLat, double fromLng, double toLat,
			double toLng) {

		double latDistance = Math.toRadians(fromLat - toLat);
		double lngDistance = Math.toRadians(fromLng - toLng);

		double a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2))
				+ (Math.cos(Math.toRadians(fromLat)))
				* (Math.cos(Math.toRadians(toLat)))
				* (Math.sin(lngDistance / 2)) * (Math.sin(lngDistance / 2));

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		return (int) (Math.round(AVERAGE_RADIUS_OF_EARTH * c * 1000));

	}

	public static HaversineConverter getInstance() {
		return instance;
	}

	/**
	 * @return the nwCornerLat
	 */
	public double getNwCornerLat() {
		return nwCornerLat;
	}

	/**
	 * @return the nwCornerLng
	 */
	public double getNwCornerLng() {
		return nwCornerLng;
	}

	/**
	 * @return the seCornerLat
	 */
	public double getSeCornerLat() {
		return seCornerLat;
	}

	/**
	 * @return the seCornerLng
	 */
	public double getSeCornerLng() {
		return seCornerLng;
	}

	/**
	 * @return the swCornerLat
	 */
	public double getSwCornerLat() {
		return swCornerLat;
	}

	/**
	 * @return the swCornerLng
	 */
	public double getSwCornerLng() {
		return swCornerLng;
	}

	/**
	 * @param nwCorner
	 *            the nwCorner to set
	 */
	public void setNwCorner(double nwCornerLat, double nwCornerLng) {
		this.nwCornerLat = nwCornerLat;
		this.nwCornerLng = nwCornerLng;
	}

	/**
	 * @param nwCorner
	 *            the nwCorner to set
	 */
	public void setSeCorner(double seCornerLat, double seCornerLng) {
		this.seCornerLat = seCornerLat;
		this.seCornerLng = seCornerLng;
	}

	/**
	 * @param nwCorner
	 *            the nwCorner to set
	 */
	public void setSwCorner(double swCornerLat, double swCornerLng) {
		this.swCornerLat = swCornerLat;
		this.swCornerLng = swCornerLng;
	}

	private int distanceNS = 0;

	private int distanceEW = 0;

	public void calculateDimension() {
		if (nwCornerLat == 0 || nwCornerLng == 0 || seCornerLat == 0
				|| seCornerLng == 0 || swCornerLat == 0 || swCornerLng == 0) {
			throw new RuntimeException(
					"Could not calculate map dimension, not all corners definded.");
		}
		distanceNS = this.calculateDistance(nwCornerLat, nwCornerLng,
				swCornerLat, swCornerLng);
		distanceEW = this.calculateDistance(seCornerLat, seCornerLng,
				swCornerLat, swCornerLng);

		log.info("Calculated dimension for map. Distance N to S is "
				+ distanceNS + "m and E to W is " + distanceEW + "m.");
	}

	public Point xyProjection(Dimension size, double longitude, double latitude) {

		int distanceWtoLat = this.calculateDistance(swCornerLat, swCornerLng,
				latitude, swCornerLng);
		int distanceNtoLng = this.calculateDistance(nwCornerLat, nwCornerLng,
				nwCornerLng, longitude);

		int x = (int) Math.round(size.getWidth() / distanceEW * distanceWtoLat);
		int y = (int) Math.round(size.getWidth() / distanceNS * distanceNtoLng);

		if (x < 0) {
			x = 0;
		}
		if (x > size.width) {
			x = size.width;
		}
		if (y < 0) {
			y = 0;
		}
		if (y > size.height) {
			y = size.height;
		}

		log.info("Map projection: WGS84 (" + longitude + "/" + latitude
				+ ") projection to Point (" + x + "/" + y + ").");

		return new Point(x, y);
	}
}
