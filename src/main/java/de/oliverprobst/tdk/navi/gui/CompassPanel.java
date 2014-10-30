package de.oliverprobst.tdk.navi.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oliverprobst.tdk.navi.controller.DiveDataProperties;

public class CompassPanel extends JPanel implements PropertyChangeListener {

	private Logger log = LoggerFactory.getLogger(CompassPanel.class);

	/**
	 * sid
	 */
	private static final long serialVersionUID = -5013078838389083700L;

	private final DecimalFormat courseFormat = new DecimalFormat("#000°");

	private final String compassRose = constructRose();

	private final JLabel lblCompass;

	private final JLabel lblCompassReturn;

	private final JLabel lblCourse;

	/**
	 * ctor
	 */
	public CompassPanel(Layouter layout) {
		layout.layout(this);

		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);

		GridBagConstraints gbc = new GridBagConstraints(0, 0, 3, 1, 1.0d, 0.0d,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						0, 0, 0, 0), 0, 0);

		lblCourse = new JLabel("");
		layout.layoutMajorLabel(lblCourse);
		this.add(lblCourse, gbc);

		gbc = new GridBagConstraints(0, 1, 1, 1, 0.0d, 0.0d,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 0), 0, 0);

		JLabel lblDescr = new JLabel("      ");
		layout.layoutDescriptionLabel(lblDescr);
		this.add(lblDescr, gbc);

		gbc = new GridBagConstraints(1, 1, 1, 1, 1.0d, 0.0d,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						0, 0, 0, 0), 0, 0);

		lblCompass = new JLabel("-| 350° | 000° | 010° |-");
		layout.layoutMicroLabel(lblCompass);
		this.add(lblCompass, gbc);

		gbc = new GridBagConstraints(2, 1, 1, 1, 0.0d, 0.0d,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 5), 5, 0);

		lblCompassReturn = new JLabel("180°");
		layout.layoutMicroLabel(lblCompassReturn);
		this.add(lblCompassReturn, gbc);
	}

	private String calculateCourseString(int course) {

		int courseRoundDown = (int) course / 10;
		int remaining = course - courseRoundDown * 10;
		int remainingToCharPointer = Math.round(remaining / (10 / 7));
		int offset = 15;
		int middle = offset + courseRoundDown * 7 + remainingToCharPointer;
		int range = 10;

		try {
			return "-{"
					+ compassRose.substring(middle - range, middle + range + 1)
					+ "}-";
		} catch (StringIndexOutOfBoundsException e) {
			log.error("Failed to generate compass String for value '" + course
					+ "'.");
			throw e;
		}

	}

	private String constructRose() {

		StringBuilder sb = new StringBuilder("340° | 350° ");
		for (int i = 0; i < 360; i += 10) {
			sb.append("| ");
			sb.append(courseFormat.format(i));
			sb.append(" ");
		}
		sb.append("| 000° | 010° | 020°");
		return sb.toString();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(DiveDataProperties.PROP_COURSE)) {
			this.setCourse((Integer) evt.getNewValue());
		}
	}

	private void setCourse(int newValue) {
		lblCourse.setText(courseFormat.format(newValue));
		lblCompass.setText(calculateCourseString(newValue));
		int retCourse = newValue - 180;
		if (retCourse < 0) {
			retCourse = 360 + retCourse;
		}

		lblCompassReturn.setText("↩ " + courseFormat.format(retCourse));
	}

}
