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

public class DepthPanel extends JPanel implements PropertyChangeListener {

	/**
	 * sid
	 */
	private static final long serialVersionUID = -5013078838389083700L;

	private final JLabel lblDepth;

	/**
	 * ctor
	 */
	public DepthPanel(Layouter layout) {
		layout.layout(this);

		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);

		GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1.0d, 1.0d,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						0, 0, 0, 0), 0, 0);

		lblDepth = new JLabel("0.00 m");
		layout.layoutMajorLabel(lblDepth);
		this.add(lblDepth, gbc);

		gbc = new GridBagConstraints(0, 1, 1, 1, 1.0d, 0.0d,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 0), 0, 0);

		JLabel lblDesc = new JLabel("Depth");
		layout.layoutDescriptionLabel(lblDesc);
		this.add(lblDesc, gbc);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(DiveDataProperties.PROP_DEPTH)) {
			this.setDepth((Float) evt.getNewValue());
		}
	}

	DecimalFormat twoDForm = new DecimalFormat("#0.00");

	private void setDepth(float newValue) {	 

		lblDepth.setText(twoDForm.format(newValue) + " m");
	}

}
