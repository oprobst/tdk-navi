package de.oliverprobst.tdk.navi;

public class NmeaParser {

	private final String ggaSplit[];

	private final boolean valid;

	public NmeaParser(String gga) {
		ggaSplit = gga.split(",");
		valid = (ggaSplit[4].length() > 0);
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

		double degree = Double.parseDouble(ggaSplit[4].substring(0, 3));
		double minutes = Double.parseDouble(ggaSplit[4].substring(3, splitLen));
		minutes = minutes / 60;

		return degree + minutes;
	}

	public double getLatitude() {
		// $GPGGA,161725.62,4764,2445,N,00921,3756,E,1,06,1.10,193.6,M,47.4,M,,*59
		if (!valid) {
			return 0;
		}
		int splitLen = ggaSplit[2].length();

		double degree = Double.parseDouble(ggaSplit[2].substring(0, 2));
		double minutes = Double.parseDouble(ggaSplit[2].substring(2, splitLen));
		minutes = minutes / 60;
		return degree + minutes;
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

	public String getDiluentOfPrecision() {
		if (!valid) {
			return "";
		}
		return ggaSplit[8];
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
