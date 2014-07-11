package de.oliverprobst.tdk.navi.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.oliverprobst.tdk.navi.controller.DiveDataProperties;

public class DiveTimePanel extends JPanel implements PropertyChangeListener {

	/**
	 * sid
	 */
	private static final long serialVersionUID = -5013078838389083700L;

	private final JLabel lblDivetime;

	private final JLabel lblSurfacetime;

	private final Layouter layout;
	
	private final JLabel lblClock;

	/**
	 * ctor
	 */
	public DiveTimePanel(Layouter layout) {

		this.layout = layout;

		layout.layout(this);

		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);

		GridBagConstraints gbc = new GridBagConstraints(0, 0, 2, 1, 1.0d, 1.0d,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						0, 0, 0, 0), 2, 0);

		lblDivetime = new JLabel("0:00");
		layout.layoutMajorLabel(lblDivetime);
		this.add(lblDivetime, gbc);

		gbc = new GridBagConstraints(1, 1, 1, 1, 1.0d, 0.0d,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 3), 2, 0);

		lblClock = new JLabel("0:00");
		layout.layoutMicroLabel(lblClock);
		this.add(lblClock, gbc);

		gbc = new GridBagConstraints(0, 1, 1, 1, 1.0d, 0.0d,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						2, 0, 0), 2, 0);

		lblSurfacetime = new JLabel("⇝ 0:00");
		layout.layoutMicroLabel(lblSurfacetime);
		this.add(lblSurfacetime, gbc);

	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(DiveDataProperties.PROP_DIVETIME)) {
			this.setDiveTime((Long) evt.getNewValue());
		} else if (evt.getPropertyName().equals(
				DiveDataProperties.PROP_SURFACETIME)) {
			this.setSurfaceTime((Long) evt.getNewValue());
		}
		Date time = new Date();
		DateFormat formatter = new SimpleDateFormat("HH:mm");
		
		lblClock.setText(formatter.format(time));

	}

	private void setDiveTime(Long newValue) {
		String divetime = String.format(
				"%d:%02d",
				TimeUnit.MILLISECONDS.toMinutes(newValue),
				TimeUnit.MILLISECONDS.toSeconds(newValue)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
								.toMinutes(newValue)));
		lblDivetime.setText(divetime);
		layout.layoutMicroLabel(lblSurfacetime);
	}

	private void setSurfaceTime(Long newValue) {
		String surfaceTime = String.format(
				"%d:%02d",
				TimeUnit.MILLISECONDS.toMinutes(newValue),
				TimeUnit.MILLISECONDS.toSeconds(newValue)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
								.toMinutes(newValue)));
		lblSurfacetime.setText("⇝ " + surfaceTime);
		lblSurfacetime.setForeground(new Color(50, 255, 50));

		
	}

}
