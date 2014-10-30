package de.oliverprobst.tdk.navi;

import java.awt.Dimension;
import java.text.DecimalFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oliverprobst.tdk.navi.dto.Location;
import de.oliverprobst.tdk.navi.gui.MapPoint;

/**
 * The Class GeoCalculator does all geo related calculation.
 */
public class GeoCalculator {

	public final static double AVERAGE_RADIUS_OF_EARTH = 6371.0;

	private static GeoCalculator instance = new GeoCalculator();

	/**
	 * @return the averageRadiusOfEarth
	 */
	public static double getAverageRadiusOfEarth() {
		return AVERAGE_RADIUS_OF_EARTH;
	}

	/**
	 * Gets the single instance of GeoCalculator.
	 *
	 * @return single instance of GeoCalculator
	 */
	public static GeoCalculator getInstance() {
		return instance;
	}

	private int distanceEW = 0;

	private int distanceNS = 0;

	/** The log. */
	private Logger log = LoggerFactory.getLogger(GeoCalculator.class);

	private double nwCornerLat = 0;

	private double nwCornerLng = 0;

	private double seCornerLat = 0;

	private double seCornerLng = 0;

	private GeoCalculator() {

	}

	/**
	 * Calculate bearing between to geo coordinates
	 *
	 * @param fromLat
	 *            the from lat
	 * @param fromLng
	 *            the from lng
	 * @param toLat
	 *            the to lat
	 * @param toLng
	 *            the to lng
	 * @return the int
	 */
	public int calculateBearing(double fromLat, double fromLng, double toLat,
			double toLng) {

		double longDiff = fromLng - toLng;
		double y = Math.sin(longDiff) * Math.cos(toLat);
		double x = Math.cos(fromLat) * Math.sin(toLat) - Math.sin(fromLat)
				* Math.cos(toLat) * Math.cos(longDiff);
		return (int) (((Math.toDegrees(Math.atan2(y, x)) + 360) % 360) + 0.5);

	}

	/**
	 * Calculate dimension of the current map.
	 */
	public void calculateDimension() {
		if (nwCornerLat == 0 || nwCornerLng == 0 || seCornerLat == 0
				|| seCornerLng == 0) {
			throw new RuntimeException(
					"Could not calculate map dimension, not all corners definded.");
		}
		// NW 47.65148/9.211642
		// SE 47.641875/9.22741

		distanceNS = this.calculateDistance(nwCornerLat, nwCornerLng,
				seCornerLat, nwCornerLng);
		distanceEW = this.calculateDistance(seCornerLat, nwCornerLng,
				seCornerLat, seCornerLng);

		log.info("Calculated dimension for map. Distance N to S is "
				+ distanceNS + "m and E to W is " + distanceEW + "m.");
	}

	/**
	 * Calculate a distance between two geo coordinates
	 * 
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
	 * Calculate new location based on current speed and bearing
	 *
	 * @param fromLat
	 *            the from lat
	 * @param fromLng
	 *            the from lng
	 * @param bearing
	 *            the bearing
	 * @param distance
	 *            the distance
	 * @return the location
	 */
	public Location calculateNewLocation(double fromLat, double fromLng,
			double bearing, double distance) {

		// from http://www.movable-type.co.uk/scripts/latlong.html

		distance = distance / 1000;
		double finalBearing = Math.toRadians(bearing);
		double angularDistance = distance / AVERAGE_RADIUS_OF_EARTH;
		double fromLatR = Math.toRadians(fromLat);
		double fromLonR = Math.toRadians(fromLng);

		double toLat = Math.asin(Math.sin(fromLatR) * Math.cos(angularDistance)
				+ Math.cos(fromLatR) * Math.sin(angularDistance)
				* Math.cos(finalBearing));
		double toLng = Math.toDegrees(((fromLonR + Math.atan2(
				Math.sin(finalBearing) * Math.sin(angularDistance)
						* Math.cos(fromLonR),
				Math.cos(angularDistance) - Math.sin(fromLatR)
						* Math.sin(toLat))) + 3 * Math.PI)
				% (2 * Math.PI) - Math.PI);
		toLat = Math.toDegrees(toLat);

		return new Location(toLat, toLng);

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

	/**
	 * Geo coordinates projection on a map image
	 *
	 * @param size
	 *            the size
	 * @param longitude
	 *            the longitude
	 * @param latitude
	 *            the latitude
	 * @return the map point
	 */
	public MapPoint xyProjection(Dimension size, double longitude,
			double latitude) {

		int distanceEtoLat = this.calculateDistance(latitude, seCornerLng,
				latitude, longitude);
		int distanceNtoLng = this.calculateDistance(nwCornerLat, longitude,
				latitude, longitude);

		int x = (int) Math.round(size.getWidth() / distanceEW
				* (distanceEW - distanceEtoLat));
		int y = (int) Math.round(size.getHeight() / distanceNS
				* (distanceNtoLng));

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

		if (log.isDebugEnabled()) {
			DecimalFormat formatLat = new DecimalFormat("#00.00000");
			DecimalFormat formatLon = new DecimalFormat("#000.00000");
			String lon = formatLon.format(longitude);
			String lat = formatLat.format(latitude);

			log.debug("Map projection: WGS84 (" + lon + "/" + lat
					+ ") with distance (" + distanceNtoLng + "/"
					+ distanceEtoLat + ") projection to Point (" + x + "/" + y
					+ ").");
		}
		return new MapPoint(x, y);
	}
}
