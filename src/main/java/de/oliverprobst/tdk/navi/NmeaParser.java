package de.oliverprobst.tdk.navi;

public class NmeaParser {

	private final String gga;

	public NmeaParser(String gga) {
		this.gga = gga;
		// todo verify (Length, Checksum)
	}

	public String getLongitude() {
		return gga.substring(19, 21) + "° " + getNSHemisphere() + " "
				+ gga.substring(21, 27);

		// gga.substring(19, 28);
	}

	public String getLatitude() {
		return gga.substring(31, 34) + "° " + getEWHemisphere() + " "
				+ gga.substring(34, 40);

		// return gga.substring(31, 41);
	}

	public String getNSHemisphere() {
		return gga.substring(29, 30);
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
