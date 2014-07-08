package de.oliverprobst.tdk.navi;

public class NmeaParser {

	private final String gga;

	public NmeaParser(String gga) {
		this.gga = gga;
		// todo verify (Length, Checksum)
	}

	public double getLongitude() {
		double degree = Double.parseDouble(gga.substring(17, 19));
		double minutes = Double.parseDouble(gga.substring(19, 21));
		double seconds = Double.parseDouble(gga.substring(22, 25));
		return convertDegreeAngleToDouble(degree, minutes, seconds); 
	}

	public double getLatitude() {

		double degree = Double.parseDouble(gga.substring(30, 33));
		double minutes = Double.parseDouble(gga.substring(33, 35));
		double seconds = Double.parseDouble(gga.substring(36, 39));
		return convertDegreeAngleToDouble(degree, minutes, seconds);
	}

	private double convertDegreeAngleToDouble(double degrees, double minutes,
			double seconds) {
		// Decimal degrees =
		// whole number of degrees,
		// plus minutes divided by 60,
		// plus seconds divided by 3600

		return degrees + (minutes / 60) + (seconds / 3600);
	}

	public String getFormattedLongitude() {
		return gga.substring(17, 19) + "° " + getNSHemisphere() + " "
				+ gga.substring(19, 25);
	}

	public String getFormattedLatitude() {
		return gga.substring(30, 33) + "° " + getEWHemisphere() + " "
				+ gga.substring(33, 39);
	}

	public String getNSHemisphere() {
		return gga.substring(28, 29);
	}

	public String getEWHemisphere() {
		return gga.substring(42, 43);
	}

	public String getSignalQuality() {
		return gga.substring(44, 45);
	}

	public String getDiluentOfPrecision() {
		return gga.substring(49, 52);
	}

	public String getSatelliteCount() {
		return gga.substring(46, 48);
	}

	public String getClock() {
		return gga.substring(7, 18);
	}
}
