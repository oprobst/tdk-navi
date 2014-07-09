package de.oliverprobst.tdk.navi;

public class NmeaParser {

	private final String ggaSplit[];

	public NmeaParser(String gga) {
		ggaSplit = gga.split(",");
		// todo verify (Length, Checksum)
	}

	public double getLongitude() {

		int splitLen = ggaSplit[4].length();

		double degree = Double.parseDouble(ggaSplit[4].substring(0, 3));
		double minutes = Double.parseDouble(ggaSplit[4].substring(3, splitLen));
		minutes = minutes / 60;

		return degree + minutes;
	}

	public double getLatitude() {
		// $GPGGA,161725.62,4764,2445,N,00921,3756,E,1,06,1.10,193.6,M,47.4,M,,*59

		int splitLen = ggaSplit[2].length();

		double degree = Double.parseDouble(ggaSplit[2].substring(0, 2));
		double minutes = Double.parseDouble(ggaSplit[2].substring(2, splitLen));
		minutes = minutes / 60;
		return degree + minutes;
	}

	public String getFormattedLongitude() {

		int splitLen = ggaSplit[4].length();
		return ggaSplit[4].substring(0, 3) + "° " + getNSHemisphere() + " "
				+ ggaSplit[4].substring(3, splitLen);
	}

	public String getFormattedLatitude() {
		int splitLen = ggaSplit[2].length();
		return ggaSplit[2].substring(0, 2) + "° " + getEWHemisphere() + " "
				+ ggaSplit[2].substring(2, splitLen);
	}

	public String getNSHemisphere() {
		return ggaSplit[3];
	}

	public String getEWHemisphere() {
		return ggaSplit[5];
	}

	public String getSignalQuality() {
		return ggaSplit[6];
	}

	public String getDiluentOfPrecision() {
		return ggaSplit[8];
	}

	public String getSatelliteCount() {
		return ggaSplit[7];
	}

	public String getClock() {
		return ggaSplit[1];
	}
}
