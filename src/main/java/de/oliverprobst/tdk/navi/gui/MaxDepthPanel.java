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

public class MaxDepthPanel extends JPanel implements PropertyChangeListener {

	/**
	 * sid
	 */
	private static final long serialVersionUID = -5013078838389083700L;

	private final JLabel lblMaxDepth;

	/**
	 * ctor
	 */
	public MaxDepthPanel(Layouter layout) {
		layout.layout(this);

		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);

		GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 0.0d, 0.0d,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 2, 2);

		lblMaxDepth = new JLabel("");
		layout.layoutMinorLabel(lblMaxDepth);
		this.add(lblMaxDepth, gbc);

		gbc = new GridBagConstraints(0, 1, 1, 1, 0.0d, 0.0d,
				GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 3), 0, 0);

		JLabel lblDesc = new JLabel("↥ max");
		layout.layoutTinyDescriptionLabel(lblDesc);
		this.add(lblDesc, gbc);

	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(DiveDataProperties.PROP_DEPTH)) {
			this.setMax((Float) evt.getNewValue());
		}
	}

	private float maximum = 0;

	private void setMax(float value) {
		if (value > maximum){
			maximum = value;

		DecimalFormat twoDForm = new DecimalFormat("#0.0");

		lblMaxDepth.setText(twoDForm.format(value) + " m");
		}
	}

}
