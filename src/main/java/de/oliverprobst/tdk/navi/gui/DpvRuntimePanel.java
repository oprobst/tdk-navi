package de.oliverprobst.tdk.navi.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.TimeUnit;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.oliverprobst.tdk.navi.controller.DiveDataProperties;

public class DpvRuntimePanel extends JPanel implements PropertyChangeListener {

	/**
	 * sid
	 */
	private static final long serialVersionUID = -5013078838389083700L;

	long lastTimestamp = 0;

	private final JLabel lblDvpRuntime;

	boolean running = false;
	long runtime = 0;

	/**
	 * ctor
	 */
	public DpvRuntimePanel(Layouter layout) {
		layout.layout(this);

		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);

		GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 0.0d, 0.0d,
				GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
				new Insets(0, 0, 0, 0), 2, 2);

		lblDvpRuntime = new JLabel("Nie");
		layout.layoutMinorLabel(lblDvpRuntime);
		this.add(lblDvpRuntime, gbc);

		gbc = new GridBagConstraints(0, 1, 1, 1, 0.0d, 0.0d,
				GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
				new Insets(0, 0, 0, 3), 2, 2);

		JLabel lblDesc = new JLabel("DPV");
		layout.layoutTinyDescriptionLabel(lblDesc);
		this.add(lblDesc, gbc);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(DiveDataProperties.PROP_SPEED)) {
			Double value = (Double) evt.getNewValue();
			if (value > 0 && !running) {
				lastTimestamp = System.currentTimeMillis();
				running = true;
			} else if (value > 0.0d && running) {
				runtime += System.currentTimeMillis() - lastTimestamp;
				lastTimestamp = System.currentTimeMillis();
			} else if (value == 0 && running) {
				runtime += System.currentTimeMillis() - lastTimestamp;
				running = false;
			}

			String dvpTime = String.format(
					"%d:%02d",
					TimeUnit.MILLISECONDS.toMinutes(runtime),
					TimeUnit.MILLISECONDS.toSeconds(runtime)
							- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
									.toMinutes(runtime)));
			lblDvpRuntime.setText(dvpTime);
		}
	}

}
