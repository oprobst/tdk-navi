package de.oliverprobst.tdk.navi.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.TimeUnit;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.oliverprobst.tdk.navi.App;
import de.oliverprobst.tdk.navi.NmeaParser;
import de.oliverprobst.tdk.navi.controller.DiveDataProperties;

public class LastFixPanel extends JPanel implements PropertyChangeListener {

	/**
	 * sid
	 */
	private static final long serialVersionUID = -5013078838389083700L;

	private final JLabel lblLastFix;

	/**
	 * ctor
	 */
	public LastFixPanel(Layouter layout) {
		layout.layout(this);

		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);

		GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 0.0d, 0.0d,
				GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
				new Insets(0, 0, 0, 0), 2, 2);

		lblLastFix = new JLabel("None");
		layout.layoutMinorLabel(lblLastFix);
		this.add(lblLastFix, gbc);

		gbc = new GridBagConstraints(0, 1, 1, 1, 0.0d, 0.0d,
				GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
				new Insets(0, 0, 0, 3), 2, 2);

		JLabel lblDesc = new JLabel("GPSfix");
		layout.layoutDescriptionLabel(lblDesc);
		this.add(lblDesc, gbc);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(DiveDataProperties.PROP_GPSFIX)) {
			this.setNewValue((NmeaParser) evt.getNewValue());
		}
		updatePanel();
	}

	private void updatePanel() {

		if (lastFixTimestamp > 0) {
			long lastFix = System.currentTimeMillis() - lastFixTimestamp;
			String lastFixString = String.format(
					"%d:%02d",
					TimeUnit.MILLISECONDS.toMinutes(lastFix),
					TimeUnit.MILLISECONDS.toSeconds(lastFix)
							- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
									.toMinutes(lastFix)));
			lblLastFix.setText(lastFixString);
		}
	}

	long lastFixTimestamp = 0;

	private void setNewValue(NmeaParser nmeaParser) {
		if (nmeaParser.getDiluentOfPrecision() >= App.getConfig().getSettings()
				.getMinGpsQuality()) {
			lastFixTimestamp = System.currentTimeMillis();
		}
	}
}
