package de.oliverprobst.tdk.navi.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.oliverprobst.tdk.navi.controller.DiveDataProperties;
import de.oliverprobst.tdk.navi.dto.PitchAndCourse;

/**
 * Draws the pitch panel.
 *  
 * @author <b></b>www.tief-dunkel-kalt.org</b><br>
 *         Oliver Probst <a
 *         href="mailto:oliverprobst@gmx.de">oliverprobst@gmx.de</a>
 * 
 * 
 */
public class PitchPanel extends JPanel implements PropertyChangeListener {

	class HorizonPanel extends AbstractNaviJPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7121296781027383584L;
		private int pitchFrontRear = 0;
		private int pitchLeftRight = 0;

		private int calculatePitchOffset(int height) {
			if (pitchFrontRear < height / 2 * -1) {
				return height / 2 * -1;
			}
			if (pitchFrontRear > height / 2) {
				return height / 2;
			}
			return pitchFrontRear;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			Graphics2D g2d = (Graphics2D) g;
			g2d.addRenderingHints(super.defineRenderingHints());

			int comWidth = g2d.getClipBounds().width;
			int comHeight = g2d.getClipBounds().height;

			final int offsetX = 10;
			final int offsetY = 1;

			g2d.setColor(new Color(100, 150, 255));

			int drawingHeight = comHeight - (offsetY * 2 - 2);
			int horizon = drawingHeight / 2;
			int offset = calculatePitchOffset(drawingHeight) * -1;

			int ypitchoffset = pitchLeftRight / 5;
			if (ypitchoffset > 15) {
				ypitchoffset = 15;
			} else if (ypitchoffset < -15) {
				ypitchoffset = -15;
			}

			// air
			g2d.fillPolygon(new int[] { offsetX + 1, offsetX + 1,
					comWidth - offsetX, comWidth - offsetX }, new int[] {
					offsetY + 1, horizon - offset + 2 - ypitchoffset,
					horizon - offset + 2 + ypitchoffset, offsetY + 1 }, 4

			);

			// ground
			g2d.setColor(new Color(10, 200, 10));
			g2d.fillPolygon(new int[] { offsetX + 1, offsetX + 1,
					comWidth - offsetX, comWidth - offsetX

			}, new int[] { drawingHeight, horizon - offset + 2 - ypitchoffset,
					horizon - offset + 2 + ypitchoffset, drawingHeight }, 4

			);

			// helper tick lines

			g2d.setColor(new Color(10, 155, 10));// ground
			for (int i = horizon - pitchFrontRear * -1 + offsetY + 1; i < comHeight
					- offsetY * 2; i += 10) {
				g2d.drawLine(offsetX + 1, i - ypitchoffset, comWidth - offsetX
						- 1, i + ypitchoffset);
			}
			g2d.setColor(new Color(10, 10, 200));// air
			for (int i = horizon - pitchFrontRear * -1 + offsetY + 1; i > offsetY + 1; i -= 10) {
				g2d.drawLine(offsetX + 1, i - ypitchoffset, comWidth - offsetX
						- 1, i + ypitchoffset);
			}

			// border and middle line
			g2d.setColor(new Color(255, 255, 255));

			g2d.drawRect(offsetX, offsetY, comWidth - offsetX * 2, comHeight
					- offsetY * 2);

			// middle line
			g2d.fillRect(offsetX - 3, offsetY + comHeight / 2, comWidth / 2
					- (6 + offsetX), 3);
			g2d.fillRect(comWidth / 2 + 1 + offsetX, offsetY + comHeight / 2,
					comWidth / 2 - (5 + offsetX), 3);
			g2d.setColor(new Color(250, 100, 100));
			g2d.fillRect(offsetX - 3, offsetY + comHeight / 2 + 1, comWidth / 2
					- (6 + offsetX), 1);
			g2d.fillRect(comWidth / 2 + 1 + offsetX, offsetY + 1 + comHeight
					/ 2, comWidth / 2 - (5 + offsetX), 1);
		}

		/**
		 * @param pitch
		 *            the pitch to set
		 */
		public void setPitch(int pitchFrontRear, int pitchLeftRight) {
			this.pitchFrontRear = pitchFrontRear;
			this.pitchLeftRight = pitchLeftRight;
			this.updateUI();
			this.repaint();

		}

	}

	/**
	 * sid
	 */
	private static final long serialVersionUID = -5013078238389083700L;
	private HorizonPanel hpanel = new HorizonPanel();
	private JLabel lblPitchFrontRear = new JLabel("");

	private JLabel lblPitchLeftRight = new JLabel("");

	/**
	 * ctor
	 */
	public PitchPanel(Layouter layout) {
		layout.layout(this);

		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);

		GridBagConstraints gbc = new GridBagConstraints(0, 0, 2, 1, 0.0d, 1.0d,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 2, 2);
		this.setMinimumSize(new Dimension(80, 80));
		hpanel.setPreferredSize(new Dimension(80, 80));
		layout.layout(hpanel, false);
		this.add(hpanel, gbc);

		gbc = new GridBagConstraints(1, 1, 1, 1, 1.0d, 0.0d,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 2), 2, 2);

		layout.layoutTinyLabel(lblPitchLeftRight);
		this.add(lblPitchLeftRight, gbc);

		gbc = new GridBagConstraints(0, 1, 1, 1, 1.0d, 0.0d,
				GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0,
						6, 0, 1), 2, 2);

		layout.layoutTinyLabel(lblPitchFrontRear);
		this.add(lblPitchFrontRear, gbc);

	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(DiveDataProperties.PROP_PITCH)) {
			PitchAndCourse newVal = (PitchAndCourse) evt.getNewValue();

			this.hpanel.setPitch(newVal.getFrontRearPitch(),
					newVal.getLeftRightPitch());
			lblPitchFrontRear.setText("↕ " + newVal.getFrontRearPitch() + "");
			lblPitchLeftRight.setText("↔ " + newVal.getLeftRightPitch() + "");
		}

	}

}
