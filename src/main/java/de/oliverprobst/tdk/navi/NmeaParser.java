package de.oliverprobst.tdk.navi;

import org.jfree.util.Log;

/**
 * The Class NmeaParser. Parses a NMEA String and provide it content as Java
 * data object.
 */
public class NmeaParser {

	private final String ggaSplit[];

	private boolean valid;

	public NmeaParser(String gga) {
		ggaSplit = gga.split(",");
		valid = (ggaSplit.length > 8 && ggaSplit[5].length() > 0)
				&& ggaSplit[0].equals("GPGGA");

		// todo verify (Length, Checksum)
	}

	public boolean isValid() {
		return valid;
	}

	public double getLongitude() {

		if (!valid) {
			return 0;
		}
		int splitLen = ggaSplit[4].length();

		try {
			double degree = Double.parseDouble(ggaSplit[4].substring(0, 3));
			double minutes = Double.parseDouble(ggaSplit[4].substring(3,
					splitLen));
			minutes = minutes / 60;

			return degree + minutes;
		} catch (NumberFormatException | StringIndexOutOfBoundsException e) {
			this.valid = false;
			return 0.0;
		}

	}

	public double getLatitude() {
		// $GPGGA,161725.62,4764,2445,N,00921,3756,E,1,06,1.10,193.6,M,47.4,M,,*59
		if (!valid) {
			return 0;
		}
		int splitLen = ggaSplit[2].length();
		try {
			double degree = Double.parseDouble(ggaSplit[2].substring(0, 2));
			double minutes = Double.parseDouble(ggaSplit[2].substring(2,
					splitLen));
			minutes = minutes / 60;
			return degree + minutes;
		} catch (NumberFormatException | StringIndexOutOfBoundsException e) {
			this.valid = false;
			return 0.0;
		}
	}

	public String getFormattedLongitude() {
		if (!valid) {
			return "No position";
		}
		int splitLen = ggaSplit[4].length();
		return ggaSplit[4].substring(0, 3) + "° " + getEWHemisphere() + " "
				+ ggaSplit[4].substring(3, splitLen);
	}

	public String getFormattedLatitude() {
		if (!valid) {
			return "";
		}
		int splitLen = ggaSplit[2].length();
		return ggaSplit[2].substring(0, 2) + "° " + getNSHemisphere() + " "
				+ ggaSplit[2].substring(2, splitLen);
	}

	public String getNSHemisphere() {
		if (!valid) {
			return "";
		}
		return ggaSplit[3];
	}

	public String getEWHemisphere() {
		if (!valid) {
			return "";
		}
		return ggaSplit[5];
	}

	public String getSignalQuality() {
		if (!valid) {
			return "X";
		}
		return ggaSplit[6];
	}

	public float getDiluentOfPrecision() {

		float dop = 99;
		if (!valid) {
			return dop;
		}
		try {
			dop = Float.parseFloat(ggaSplit[8]);
		} catch (NumberFormatException e) {
			Log.warn("Invalid dop: " + dop);
		}
		return dop;

	}

	public String getSatelliteCount() {
		if (!valid) {
			return "";
		}
		return ggaSplit[7];
	}

	public String getClock() {
		if (!valid) {
			return "";
		}
		return ggaSplit[1];
	}
}
