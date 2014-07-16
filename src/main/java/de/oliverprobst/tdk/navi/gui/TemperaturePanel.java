package de.oliverprobst.tdk.navi.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.oliverprobst.tdk.navi.controller.DiveDataProperties;

public class TemperaturePanel extends JPanel implements PropertyChangeListener {

	/**
	 * sid
	 */
	private static final long serialVersionUID = -5013078838389083700L;

	private final JLabel lblTemperature;

	/**
	 * ctor
	 */
	public TemperaturePanel(Layouter layout) {
		layout.layout(this);

		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);

		GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 0.0d, 0.0d,
				GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
				new Insets(0, 0, 0, 0), 2, 2);

		lblTemperature = new JLabel("Nie");
		layout.layoutMinorLabel(lblTemperature);
		this.add(lblTemperature, gbc);

		gbc = new GridBagConstraints(0, 1, 1, 1, 0.0d, 0.0d,
				GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
				new Insets(0, 0, 0, 3), 2, 2);

		JLabel lblDesc = new JLabel("°C");
		layout.layoutDescriptionLabel(lblDesc);
		this.add(lblDesc, gbc);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(DiveDataProperties.PROP_TEMPERATUR)) {
			this.setNewValue((Float) evt.getNewValue());
		}

	}

	long lastFixTimestamp = 0;

	private DecimalFormat degreeFormatter = new DecimalFormat("#0.0");

	private void setNewValue(float newValue) {

		lblTemperature.setText(degreeFormatter.format(newValue));

	}
}
