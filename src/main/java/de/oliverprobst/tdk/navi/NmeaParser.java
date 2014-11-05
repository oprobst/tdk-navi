package de.oliverprobst.tdk.navi;

/**
 * The Class NmeaParser. Parses a NMEA String and provide it content as Java
 * data object. Validates also for content.
 * 
 * @author <b></b>www.tief-dunkel-kalt.org</b><br>
 *         Oliver Probst <a
 *         href="mailto:oliverprobst@gmx.de">oliverprobst@gmx.de</a>
 */
public class NmeaParser {

	/** The diluent of precision */
	private float dop = 99f;

	/** A preformatted default for latitude string. */
	private String formattedLatitude = "No position";
	
	/** A preformatted default for longitude string. */
	private String formattedLongitude = "";

	/** The values of the nmea string */
	private final String ggaSplit[];

	/** The latitude as provided by the NMEA String */
	private double latitude = 0;

	/** The longitude as provided by the NMEA String */
	private double longitude = 0;

	/** The quality of the signal */
	private int quality = 0;

	/** Indicator if this is a valid GGA NMEA String */
	private boolean valid = false;

	/**
	 * Instantiates a new nmea parser.
	 * 
	 * Split and verify content of payload. Evaluate validity. All values are
	 * determined on instance creation and are cached.
	 *
	 * @param gga
	 *            The payload string from the GPS.
	 */
	public NmeaParser(String gga) {
		ggaSplit = gga.split(",");
		valid = ggaSplit.length > 8 && ggaSplit[4].length() > 3
				&& ggaSplit[2].length() > 3 && ggaSplit[0].equals("GPGGA");
		// $GPGGA,161725.62,4764,2445,N,00921,3756,E,1,06,1.10,193.6,M,47.4,M,,*59

		if (valid) {

			int splitLen = ggaSplit[4].length();

			try {
				double degree = Double.parseDouble(ggaSplit[4].substring(0, 3));
				double minutes = Double.parseDouble(ggaSplit[4].substring(3,
						splitLen));
				minutes = minutes / 60;

				longitude = degree + minutes;
			} catch (NumberFormatException | StringIndexOutOfBoundsException e) {
				this.valid = false;
				longitude = 0.0;
			}

			splitLen = ggaSplit[2].length();
			try {
				double degree = Double.parseDouble(ggaSplit[2].substring(0, 2));
				double minutes = Double.parseDouble(ggaSplit[2].substring(2,
						splitLen));
				minutes = minutes / 60;
				latitude = degree + minutes;
			} catch (NumberFormatException | StringIndexOutOfBoundsException e) {
				this.valid = false;
				latitude = 0.0;
			}
			try {
				splitLen = ggaSplit[4].length();
				formattedLongitude = ggaSplit[4].substring(0, 3) + "° "
						+ getEWHemisphere() + " "
						+ ggaSplit[4].substring(3, splitLen);

				splitLen = ggaSplit[2].length();
				formattedLatitude = ggaSplit[2].substring(0, 2) + "° "
						+ getNSHemisphere() + " "
						+ ggaSplit[2].substring(2, splitLen);
			} catch (StringIndexOutOfBoundsException e) {
				this.valid = false;
				formattedLatitude = "";
				formattedLongitude = "";
			}
			try {
				dop = Float.parseFloat(ggaSplit[8]);
			} catch (NumberFormatException e) {
				this.valid = false;
				dop = 99.0f;
			}
			try {
				quality = Integer.parseInt(ggaSplit[6]);
			} catch (NumberFormatException e) {
				this.valid = false;
				quality = 0;
			}

		}
	}

	/**
	 * Gets the clock as provided by the GPS (usually UTC)
	 *
	 * @return the clock
	 */
	public String getClock() {
		if (!valid) {
			return "";
		}
		return ggaSplit[1];
	}

	/**
	 * Gets the diluent of precision.
	 *
	 * @return the diluent of precision
	 */
	public float getDiluentOfPrecision() {

		return dop;

	}

	/**
	 * Gets the EW hemisphere.
	 *
	 * @return the EW hemisphere
	 */
	public String getEWHemisphere() {
		if (!valid) {
			return "";
		}
		return ggaSplit[5];
	}

	/**
	 * Gets the a preformatted default for latitude string.
	 *
	 * @return the a preformatted default for latitude string
	 */
	public String getFormattedLatitude() {
		return formattedLatitude;
	}

	/**
	 * Gets the a preformatted default for longitude string.
	 *
	 * @return the a preformatted default for longitude string
	 */
	public String getFormattedLongitude() {
		return formattedLongitude;
	}

	/**
	 * Gets the latitude as provided by the NMEA String.
	 *
	 * @return the latitude as provided by the NMEA String
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * Gets the longitude as provided by the NMEA String.
	 *
	 * @return the longitude as provided by the NMEA String
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * Gets the NS hemisphere.
	 *
	 * @return the NS hemisphere
	 */
	public String getNSHemisphere() {
		if (!valid) {
			return "";
		}
		return ggaSplit[3];
	}

	/**
	 * Gets the satellite count.
	 *
	 * @return the satellite count
	 */
	public String getSatelliteCount() {
		if (!valid) {
			return "";
		}
		return ggaSplit[7];
	}

	/**
	 * Gets the signal quality.
	 *
	 * @return the signal quality
	 */
	public int getSignalQuality() {
		return quality;
	}

	/**
	 * Checks if is indicator if this is a valid GGA NMEA String.
	 *
	 * @return the indicator if this is a valid GGA NMEA String
	 */
	public boolean isValid() {
		return valid;
	}
}
