package de.oliverprobst.tdk.navi.gui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.oliverprobst.tdk.navi.controller.DiveDataProperties;

public class NotesPanel extends JPanel implements PropertyChangeListener {

	/**
	 * sid
	 */
	private static final long serialVersionUID = -5013878838389083700L;

	private final JLabel lblNotes;

	/**
	 * ctor
	 */
	public NotesPanel(Layouter layout) {
		layout.layout(this);

		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);

		GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 0.0d, 0.0d,
				GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5,
						0, 0, 0), 0, 0);

		lblNotes = new JLabel("Dive notes");
		lblNotes.setFont(new Font("Dialog", Font.PLAIN, 10));
		layout.layoutMicroLabel(lblNotes);
		this.add(lblNotes, gbc);

	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(DiveDataProperties.PROP_NOTES)) {
			this.setNotes((String) evt.getNewValue());
		}
	}

	public void setNotes(String notes) {
		lblNotes.setText(notes);
	}

}
