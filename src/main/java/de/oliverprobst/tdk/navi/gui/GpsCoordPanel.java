package de.oliverprobst.tdk.navi.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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

	private final JLabel lblSat = new JLabel("");
	private final JLabel lblPrecision = new JLabel("");

	/**
	 * ctor
	 */
	public GpsCoordPanel(Layouter layout) {
		layout.layout(this);

		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);

		GridBagConstraints gbc = new GridBagConstraints(0, 0, 2, 1, 0.0d, 0.0d,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 0), 2, 2);

		lblNSHemisphere = new JLabel("No GPS");
		layout.layoutMinorLabel(lblNSHemisphere);
		this.add(lblNSHemisphere, gbc);

		gbc = new GridBagConstraints(0, 1, 2, 1, 0.0d, 0.0d,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 0), 2, 2);

		lblEWHemisphere = new JLabel("Signal");
		layout.layoutMinorLabel(lblEWHemisphere);
		this.add(lblEWHemisphere, gbc);

		gbc = new GridBagConstraints(0, 2, 1, 1, 0.0d, 0.0d,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 3), 2, 2);

		layout.layoutMicroLabel(lblPrecision);
		this.add(lblPrecision, gbc);

		gbc = new GridBagConstraints(1, 2, 1, 1, 0.0d, 0.0d,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 3), 2, 2);

		layout.layoutMicroLabel(lblSat);
		this.add(lblSat, gbc);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(DiveDataProperties.PROP_GPSFIX)) {
			this.updateLocation((String) evt.getNewValue());
		}
	}

	private void updateLocation(String locationGgaString) {
		NmeaParser p = new NmeaParser(locationGgaString);
		lblNSHemisphere.setText(p.getFormattedLongitude());
		lblEWHemisphere.setText(p.getFormattedLatitude());
		lblSat.setText(p.getSatelliteCount() + " Sat");
		lblPrecision.setText(p.getDiluentOfPrecision() + " DOP");
	}

}
