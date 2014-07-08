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

public class VoltagePanel extends JPanel implements PropertyChangeListener {

	/**
	 * sid
	 */
	private static final long serialVersionUID = -5013078838389083700L;

	private final JLabel lblVoltage;

	/**
	 * ctor
	 */
	public VoltagePanel(Layouter layout) {
		layout.layout(this);

		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);

		GridBagConstraints gbc = new GridBagConstraints(0, 0, 2, 1, 0.0d, 0.0d,
				GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
				new Insets(0, 0, 0, 0), 2, 2);

		lblVoltage = new JLabel("0.0");
		layout.layoutMicroLabel(lblVoltage);
		this.add(lblVoltage, gbc);

		gbc = new GridBagConstraints(1, 1, 1, 1, 0.0d, 0.0d,
				GridBagConstraints.SOUTHEAST, GridBagConstraints.VERTICAL,
				new Insets(0, 0, 0, 3), 2, 2);

		JLabel lblDesc = new JLabel("V");
		layout.layoutTinyDescriptionLabel(lblDesc);
		this.add(lblDesc, gbc);
	}

	DecimalFormat voltageFormatter = new DecimalFormat("#0.00");

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(DiveDataProperties.PROP_VOLTAGE)) {
			lblVoltage.setText(voltageFormatter.format((Float) evt
					.getNewValue()));

		}
	}

}
