package de.oliverprobst.tdk.navi.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.TimeUnit;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.oliverprobst.tdk.navi.NmeaParser;
import de.oliverprobst.tdk.navi.controller.DiveDataProperties;

public class GpsCoordPanel extends JPanel implements PropertyChangeListener {

	/**
	 * sid
	 */
	private static final long serialVersionUID = -5013078838389083700L;

	private final JLabel lblNSHemisphere;
	private final JLabel lblEWHemisphere;

	private final JLabel lblLastFix = new JLabel("");
	private final JLabel lblSat = new JLabel("");
	private final JLabel lblPrecision = new JLabel("");
	long lastFixTimestamp = 0;

	/**
	 * ctor
	 */
	public GpsCoordPanel(Layouter layout) {
		layout.layout(this);

		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);

		GridBagConstraints gbc = new GridBagConstraints(0, 0, 3, 1, 0.0d, 0.0d,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 0), 2, 2);

		lblNSHemisphere = new JLabel("No GPS");
		layout.layoutMinorLabel(lblNSHemisphere);
		this.add(lblNSHemisphere, gbc);

		gbc = new GridBagConstraints(0, 1, 3, 1, 0.0d, 0.0d,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 0), 2, 2);

		lblEWHemisphere = new JLabel("Signal");
		layout.layoutMinorLabel(lblEWHemisphere);
		this.add(lblEWHemisphere, gbc);

		gbc = new GridBagConstraints(0, 2, 1, 1, 0.3d, 0.0d,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,
						1, 0, 0), 0, 0);

		layout.layoutMicroLabel(lblPrecision);
		this.add(lblPrecision, gbc);

		gbc = new GridBagConstraints(1, 2, 1, 1, 0.3d, 0.0d,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						0, 0, 0, 0), 0, 0);

		layout.layoutMicroLabel(lblLastFix);
		this.add(lblLastFix, gbc);

		gbc = new GridBagConstraints(2, 2, 1, 1, 0.3d, 0.0d,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 1), 0, 0);

		layout.layoutMicroLabel(lblSat);
		this.add(lblSat, gbc);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(DiveDataProperties.PROP_GPSFIX)) {
			this.updateLocation((NmeaParser) evt.getNewValue());
			lastFixTimestamp = System.currentTimeMillis();
		}
		updateLastFixLabel();
	}

	private void updateLastFixLabel() {

		if (lastFixTimestamp > 0) {
			long lastFix = System.currentTimeMillis() - lastFixTimestamp;
			String lastFixString = String.format(
					"%d:%02d",
					TimeUnit.MILLISECONDS.toMinutes(lastFix),
					TimeUnit.MILLISECONDS.toSeconds(lastFix)
							- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
									.toMinutes(lastFix)));
			lblLastFix.setText("⌛" + lastFixString);

			if (lastFix > 15000) {
				int red = 255;
				int notRed = (int) (220 - 220 * lastFix / 600000);
				if (notRed < 0) {
					notRed = 0;
				}
				Color ageColor = new Color(red, notRed, notRed);
				lblNSHemisphere.setForeground(ageColor);
				lblEWHemisphere.setForeground(ageColor);
				lblSat.setForeground(ageColor);
				lblPrecision.setForeground(ageColor);
				lblLastFix.setVisible(true);
			} else {
				lblLastFix.setVisible(false);
			}
		}
	}

	private void updateLocation(NmeaParser nmea) { 
		if (!nmea.isValid()) {
			return;
		}
		int gpsQuality = nmea.getSignalQuality();
		if (gpsQuality > 0) {
			String longitude = nmea.getFormattedLongitude();
			longitude = longitude.substring(0, longitude.length() - 2);
			String latitude = nmea.getFormattedLatitude();
			latitude = latitude.substring(0, latitude.length() - 2);
			lblNSHemisphere.setText(latitude);
			lblEWHemisphere.setText(longitude);
			lblPrecision.setText("✅" + nmea.getDiluentOfPrecision());
			if (gpsQuality > 1) {
				Color lightGreen = new Color(150, 250, 200);
				lblNSHemisphere.setForeground(lightGreen);
				lblEWHemisphere.setForeground(lightGreen);
				lblPrecision.setForeground(lightGreen);
				lblSat.setForeground(Color.CYAN);

			} else {
				lblNSHemisphere.setForeground(Color.WHITE);
				lblEWHemisphere.setForeground(Color.WHITE);
				lblPrecision.setForeground(Color.WHITE);
				lblSat.setForeground(Color.WHITE);
			}
			lblSat.setText("✢" + nmea.getSatelliteCount());
		}
	}

}
