package de.oliverprobst.tdk.navi.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.oliverprobst.tdk.navi.controller.DiveDataProperties;

public class PitchPanel extends JPanel implements PropertyChangeListener {

	/**
	 * sid
	 */
	private static final long serialVersionUID = -5013078238389083700L;

	private HorizonPanel hpanel = new HorizonPanel();
	private JLabel lblPitch = new JLabel("");

	/**
	 * ctor
	 */
	public PitchPanel(Layouter layout) {
		layout.layout(this);

		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);

		GridBagConstraints gbc = new GridBagConstraints(0, 0, 2, 1, 0.0d, 0.0d,
				GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
				new Insets(0, 0, 0, 0), 2, 2);
		hpanel.setPreferredSize(new Dimension(60, 60));
		layout.layout(hpanel);
		this.add(hpanel, gbc);

		gbc = new GridBagConstraints(1, 1, 1, 1, 0.0d, 0.0d,
				GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
				new Insets(0, 0, 0, 2), 2, 2);

		JLabel lblDesc = new JLabel("Pitch");
		layout.layoutTinyDescriptionLabel(lblDesc);
		this.add(lblDesc, gbc);

		gbc = new GridBagConstraints(0, 1, 1, 1, 0.0d, 0.0d,
				GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
				new Insets(0, 6, 0, 1), 2, 2);

		layout.layoutTinyLabel(lblPitch);
		this.add(lblPitch, gbc);

	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(DiveDataProperties.PROP_PITCH)) {
			int newVal = (Integer) evt.getNewValue();
			this.hpanel.setPitch(newVal);
			lblPitch.setText(newVal + " %");
		}

	}

	class HorizonPanel extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7121296781027383584L;
		private int pitch = 0;

		/**
		 * @return the pitch
		 */
		public int getPitch() {
			return pitch;
		}

		/**
		 * @param pitch
		 *            the pitch to set
		 */
		public void setPitch(int pitch) {
			this.pitch = pitch;
			this.updateUI();
			this.repaint();

		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			int comWidth = g.getClipBounds().width;
			int comHeight = g.getClipBounds().height;

			final int offsetX = 10;
			final int offsetY = 1;

			g.setColor(new Color(100, 150, 255));

			int drawingHeight = comHeight - (offsetY * 2 - 2);
			int horizon = drawingHeight / 2;
			int offset = calculatePitchOffset(drawingHeight) * -1;

			// air
			g.fillRect(offsetX + 1, offsetY + 1, comWidth - offsetX * 2 - 1,
					horizon - offset);

			// ground
			g.setColor(new Color(10, 200, 10));
			g.fillRect(offsetX + 1, horizon - offset + offsetY + 1, comWidth
					- offsetX * 2 - 1, horizon + offset);

			// helper tick lines
			g.setColor(new Color(10, 155, 10));// ground
			for (int i = horizon - pitch * -1 + offsetY + 1; i < comHeight
					- offsetY * 2; i += 10) {
				g.drawLine(offsetX + 1, i, comWidth - offsetX - 1, i);
			}
			g.setColor(new Color(10, 10, 200));// air
			for (int i = horizon - pitch * -1 + offsetY + 1; i > offsetY + 1; i -= 10) {
				g.drawLine(offsetX + 1, i, comWidth - offsetX - 1, i);
			}

			// border and middle line
			g.setColor(new Color(255, 255, 255));

			g.drawRect(offsetX, offsetY, comWidth - offsetX * 2, comHeight
					- offsetY * 2);

			// middle line
			g.fillRect(offsetX - 3, offsetY + comHeight / 2, comWidth / 2
					- (6 + offsetX), 3);
			g.fillRect(comWidth / 2 + 1 + offsetX, offsetY + comHeight / 2,
					comWidth / 2 - (5 + offsetX), 3);
			g.setColor(new Color(250, 100, 100));
			g.fillRect(offsetX - 3, offsetY + comHeight / 2 + 1, comWidth / 2
					- (6 + offsetX), 1);
			g.fillRect(comWidth / 2 + 1 + offsetX, offsetY + 1 + comHeight / 2,
					comWidth / 2 - (5 + offsetX), 1);
		}

		private int calculatePitchOffset(int height) {
			if (pitch < height / 2 * -1) {
				return height / 2 * -1;
			}
			if (pitch > height / 2) {
				return height / 2;
			}
			return pitch;
		}

	}

}
