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
	private final JLabel lblMaxDepth;
	private final JLabel lblAvgDepth;
	private float maximum = 0;

	DecimalFormat twoDForm = new DecimalFormat("#0.00");

	/**
	 * ctor
	 */
	public DepthPanel(Layouter layout) {
		layout.layout(this);

		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);

		GridBagConstraints gbc = new GridBagConstraints(0, 0, 2, 1, 1.0d, 1.0d,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						0, 0, 0, 0), 0, 0);

		lblDepth = new JLabel("0.00 m");
		layout.layoutMajorLabel(lblDepth);
		this.add(lblDepth, gbc);

		gbc = new GridBagConstraints(0, 1, 1, 1, 1.0d, 0.0d,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 0), 0, 0);

		lblMaxDepth = new JLabel("↓ 0.0m");
		layout.layoutMicroLabel(lblMaxDepth);
		this.add(lblMaxDepth, gbc);

		gbc = new GridBagConstraints(1, 1, 1, 1, 1.0d, 0.0d,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 0), 0, 0);

		lblAvgDepth = new JLabel("∅ 0.0m");
		layout.layoutMicroLabel(lblAvgDepth);
		this.add(lblAvgDepth, gbc);

	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(DiveDataProperties.PROP_DEPTH)) {
			float depth = (Float) evt.getNewValue();
			this.setDepth(depth);
			this.setAvg(depth);
			setMax(depth);
		}
	}

	private void setDepth(float newValue) {
		lblDepth.setText(twoDForm.format(newValue) + " m");
	}

	private void setMax(float value) {
		if (value > maximum) {
			maximum = value;

			DecimalFormat twoDForm = new DecimalFormat("0.0");
			lblMaxDepth.setText("↥ " + twoDForm.format(value) + ""); // ↥
		}
	}

	float curAvg = 0;
	long curAvgCount = 1;
	float curAvg10 = 0;
	long curAvgCount10 = 1;
	DecimalFormat depthFormat = new DecimalFormat("0.0");

	private void setAvg(float newValue) {
		curAvg = (curAvg * curAvgCount + newValue) / ++curAvgCount;
		String avg = "∅:" + depthFormat.format(curAvg) + "";

		if (newValue > 10) {
			curAvg10 = (curAvg10 * curAvgCount10 + newValue) / ++curAvgCount10;

			avg += ( " ∅↓:"+ depthFormat.format(curAvg10) + "");

		}
		lblAvgDepth.setText(avg);
	}

}
