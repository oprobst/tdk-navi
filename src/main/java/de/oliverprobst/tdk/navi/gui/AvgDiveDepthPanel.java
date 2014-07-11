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

public class AvgDiveDepthPanel extends JPanel implements PropertyChangeListener {

	/**
	 * sid
	 */
	private static final long serialVersionUID = -5013078838389083700L;

	private final JLabel lblAvgDownTime;
	private final JLabel lblAvg10 = new JLabel("0.0 ");

	/**
	 * ctor
	 */
	public AvgDiveDepthPanel(Layouter layout) {
		layout.layout(this);

		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);

		GridBagConstraints gbc = new GridBagConstraints(0, 0, 2, 1, 1.0d, 0.0d,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 2, 2);

		lblAvgDownTime = new JLabel("0");
		layout.layoutMinorLabel(lblAvgDownTime);
		this.add(lblAvgDownTime, gbc);

		gbc = new GridBagConstraints(0, 1, 1, 1, 1.0d, 0.0d,
				GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(2,
						0, 0, 3), 0, 0);

		JLabel lblDesc = new JLabel("<html>avg↓<br/>↓10m</html>");
		layout.layoutTinyDescriptionLabel(lblDesc);
		this.add(lblDesc, gbc);

		gbc = new GridBagConstraints(1, 1, 1, 1, 0.0d, 0.0d,
				GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
				new Insets(0, 0, 0, 3), 2, 2);

		layout.layoutMicroLabel(lblAvg10);
		this.add(lblAvg10, gbc);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(DiveDataProperties.PROP_DEPTH)) {
			this.setNewValue((Float) evt.getNewValue());
		}
	}

	float curAvg = 0;
	long curAvgCount = 1;
	float curAvg10 = 0;
	long curAvgCount10 = 1;
	DecimalFormat depthFormat = new DecimalFormat("0.0");

	private void setNewValue(float newValue) {
		curAvg = (curAvg * curAvgCount + newValue) / ++curAvgCount;
		if (newValue > 10) {
			curAvg10 = (curAvg10 * curAvgCount10 + newValue) / ++curAvgCount10;
			lblAvg10.setText(depthFormat.format(curAvg10));
		}
		lblAvgDownTime.setText(depthFormat.format(curAvg) + " m");
	}
}
