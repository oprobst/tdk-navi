package de.oliverprobst.tdk.navi;

public class NmeaParser {

	private final String gga;

	public NmeaParser(String gga) {
		this.gga = gga;
		// todo verify (Length, Checksum)
	}

	public double getLongitude() {
		double degree = Double.parseDouble(gga.substring(17, 19));
		double minutes = Double.parseDouble(gga.substring(19, 26).replace(",", ""));
		minutes = minutes / 600000 ;
		double out = degree+minutes;
			 
		return out;
		
	}

	public double getLatitude() {
//$GPGGA,161725.62,4764,2445,N,00921,3756,E,1,06,1.10,193.6,M,47.4,M,,*59
		double degree = Double.parseDouble(gga.substring(29, 32));
		double minutes = Double.parseDouble(gga.substring(32, 39).replace(",", ""));
		minutes = minutes / 600000 ;
		double out = degree+minutes;
		 
		return out;
	}

	private double convertDegreeAngleToDouble(double degrees, double minutes) {
		// Decimal degrees =
		// whole number of degrees,
		// plus minutes divided by 60,
		// plus seconds divided by 3600

		return degrees + (minutes / 60) ;
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
