package de.oliverprobst.tdk.navi;

/**
 * The Class NmeaParser. Parses a NMEA String and provide it content as Java
 * data object.
 */
public class NmeaParser {

	private float dop = 99f;

	private String formattedLatitude = "No position";
	private String formattedLongitude = "";
	private final String ggaSplit[];
	private double latitude = 0;
	private double longitude = 0;
	private boolean valid = false;
	private int quality = 0;

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
		valid = (ggaSplit.length > 8 && ggaSplit[5].length() > 0)
				&& ggaSplit[0].equals("GPGGA");
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

			splitLen = ggaSplit[4].length();
			formattedLongitude = ggaSplit[4].substring(0, 3) + "° "
					+ getEWHemisphere() + " "
					+ ggaSplit[4].substring(3, splitLen);

			splitLen = ggaSplit[2].length();
			formattedLatitude = ggaSplit[2].substring(0, 2) + "° "
					+ getNSHemisphere() + " "
					+ ggaSplit[2].substring(2, splitLen);

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

	public String getClock() {
		if (!valid) {
			return "";
		}
		return ggaSplit[1];
	}

	public float getDiluentOfPrecision() {

		return dop;

	}

	public String getEWHemisphere() {
		if (!valid) {
			return "";
		}
		return ggaSplit[5];
	}

	public String getFormattedLatitude() {
		return formattedLatitude;
	}

	public String getFormattedLongitude() {
		return formattedLongitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public String getNSHemisphere() {
		if (!valid) {
			return "";
		}
		return ggaSplit[3];
	}

	public String getSatelliteCount() {
		if (!valid) {
			return "";
		}
		return ggaSplit[7];
	}

	public int getSignalQuality() {
		return quality;
	}

	public boolean isValid() {
		return valid;
	}
}
