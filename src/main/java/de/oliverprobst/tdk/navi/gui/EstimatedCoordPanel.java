package de.oliverprobst.tdk.navi.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.oliverprobst.tdk.navi.controller.DiveDataProperties;
import de.oliverprobst.tdk.navi.dto.Location;

public class EstimatedCoordPanel extends JPanel implements
		PropertyChangeListener {

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
	Location estimatedLocation = null;

	/**
	 * ctor
	 */
	public EstimatedCoordPanel(Layouter layout) {
		layout.layout(this);

		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);

		GridBagConstraints gbc = new GridBagConstraints(0, 0, 3, 1, 0.0d, 0.0d,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 0), 2, 2);

		lblNSHemisphere = new JLabel("No Estimation");
		layout.layoutMinorLabel(lblNSHemisphere);
		this.add(lblNSHemisphere, gbc);

		gbc = new GridBagConstraints(0, 1, 3, 1, 0.0d, 0.0d,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 0), 2, 2);

		lblEWHemisphere = new JLabel("");
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

		Color lightBlue = new Color(180, 180, 250);
		lblNSHemisphere.setForeground(lightBlue);
		lblEWHemisphere.setForeground(lightBlue);
		lblPrecision.setForeground(lightBlue);

		this.add(lblSat, gbc);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(DiveDataProperties.PROP_ESTIMATED)) {
			updateLocation((Location) evt.getNewValue());

		}

		if (evt.getPropertyName().equals(DiveDataProperties.PROP_GPSFIX)) {
			lastFixTimestamp = System.currentTimeMillis();

		}
	}

	private void updateLocation(Location location) {
		lblNSHemisphere.setText(location.getFormattedLatitude());
		lblEWHemisphere.setText(location.getFormattedLongitude());
	}

}
