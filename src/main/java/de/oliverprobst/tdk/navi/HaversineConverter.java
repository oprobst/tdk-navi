package de.oliverprobst.tdk.navi;

import java.awt.Dimension;
import java.awt.Point;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HaversineConverter {

	/**
	 * @return the averageRadiusOfEarth
	 */
	public static double getAverageRadiusOfEarth() {
		return AVERAGE_RADIUS_OF_EARTH;
	}

	public static HaversineConverter getInstance() {
		return instance;
	}

	private Logger log = LoggerFactory.getLogger(App.class);

	public final static double AVERAGE_RADIUS_OF_EARTH = 6371;

	private static HaversineConverter instance = new HaversineConverter();

	private double nwCornerLat = 0;

	private double nwCornerLng = 0;

	private double seCornerLat = 0;

	private double seCornerLng = 0;

	private int distanceNS = 0;

	private int distanceEW = 0;

	private HaversineConverter() {

	}

	public void calculateDimension() {
		if (nwCornerLat == 0 || nwCornerLng == 0 || seCornerLat == 0
				|| seCornerLng == 0) {
			throw new RuntimeException(
					"Could not calculate map dimension, not all corners definded.");
		}
		// NW 47.65148/9.211642
		// SE 47.641875/9.22741

		 distanceEW= this.calculateDistance(nwCornerLat, nwCornerLng,
				seCornerLat, nwCornerLng);
		 distanceNS = this.calculateDistance(seCornerLat, nwCornerLng,
				seCornerLat, seCornerLng);

		log.info("Calculated dimension for map. Distance N to S is "
				+ distanceNS + "m and E to W is " + distanceEW + "m.");
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

	public Point xyProjection(Dimension size, double longitude, double latitude) {

		int distanceEtoLat = this.calculateDistance(seCornerLat, longitude,
				latitude, longitude);
		int distanceNtoLng = distanceNS
				- this.calculateDistance(latitude, nwCornerLng, latitude,
						longitude);

		int x = (int) Math.round(size.getWidth() / distanceEW * distanceEtoLat);
		int y = (int) Math
				.round(size.getHeight() / distanceNS * distanceNtoLng);

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

		log.debug("Map projection: WGS84 (" + longitude + "/" + latitude
				+ ") projection to Point (" + x + "/" + y + ").");

		return new Point(x, y);
	}
}
