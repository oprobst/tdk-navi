package de.oliverprobst.tdk.navi.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Polygon;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.oliverprobst.tdk.navi.controller.DiveDataProperties;
import de.oliverprobst.tdk.navi.dto.StructuralIntegrity;
import de.oliverprobst.tdk.navi.dto.StructuralIntegrity.Status;

public class StructureIntegrityPanel extends JPanel implements
		PropertyChangeListener {

	/**
	 * sid
	 */
	private static final long serialVersionUID = -5013078238389083700L;

	private HullStatusPanel hullStatusPanel = new HullStatusPanel();
	private JLabel lblPressure = new JLabel();

	/**
	 * ctor
	 */
	public StructureIntegrityPanel(Layouter layout) {
		layout.layout(this);

		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);

		GridBagConstraints gbc = new GridBagConstraints(0, 0, 2, 1, 0.0d, 0.0d,
				GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
				new Insets(0, 0, 0, 0), 2, 2);
		hullStatusPanel.setPreferredSize(new Dimension(38, 20));
		layout.layout(hullStatusPanel);
		this.add(hullStatusPanel, gbc);

		gbc = new GridBagConstraints(1, 1, 1, 1, 0.0d, 0.0d,
				GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
				new Insets(0, 0, 0, 2), 2, 2);

		JLabel lblDesc = new JLabel("hPa");
		layout.layoutTinyDescriptionLabel(lblDesc);
		this.add(lblDesc, gbc);

		gbc = new GridBagConstraints(0, 1, 1, 1, 0.0d, 0.0d,
				GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
				new Insets(0, 6, 0, 1), 2, 2);

		layout.layoutTinyLabel(lblPressure);
		this.add(lblPressure, gbc);

	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(DiveDataProperties.PROP_HULL)) {
			StructuralIntegrity newVal = (StructuralIntegrity) evt
					.getNewValue();
			this.hullStatusPanel.setHullIntegrity(newVal);
			lblPressure.setText(String.valueOf(newVal.getPressure()));
		}

	}

	class HullStatusPanel extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7121296781027383584L;
		private StructuralIntegrity hullIntegrity = null;

		/**
		 * @return the hullIntegrity
		 */
		public StructuralIntegrity getHullIntegrity() {
			return hullIntegrity;
		}

		/**
		 * @param hullIntegrity
		 *            the hullIntegrity to set
		 */
		public void setHullIntegrity(StructuralIntegrity hullIntegrity) {
			this.hullIntegrity = hullIntegrity;
			this.updateUI();
			this.repaint();
		}

		private final Color okColor = new Color(0, 255, 0);
		private final Color problemColor = new Color(255, 255, 00);
		private final Color brokenColor = new Color(250, 50, 00);

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			if (hullIntegrity != null) {

				int comWidth = g.getClipBounds().width;

				final int size = 12;
				final int dist = 2;
				final Color border = new Color(150, 150, 150);
				final Color font = new Color(50, 50, 250);
				g.setColor(new Color(100, 150, 255));
				g.setFont(new Font("Dialog", Font.BOLD, 9));

				// paint dpv
				g.setColor(new Color(100, 100, 100));
				g.fillRect(0, size / 2 - 3, comWidth, 6);
				g.setColor(new Color(180, 180, 180));
				g.drawLine(0, 0, 0, size);
				g.drawRect(0, size / 2 - 3, comWidth, 6);

				// stern
				setColor(g, hullIntegrity.getStern());
				g.fillArc(0, 0, size, size, 0, 360);
				g.setColor(font);
				g.drawString("S", 3, 11);
				g.setColor(border);
				g.drawArc(0, 0, size, size, 0, 360);

				// ambient
				setColor(g, hullIntegrity.getAmbient());

				int xPoly[] = { size + dist, size * 2 + 1 + dist,
						size + size / 2 - 1 + dist, size + size / 2 + dist };
				int yPoly[] = { size, size, 0, 0 };

				g.fillPolygon(new Polygon(xPoly, yPoly, xPoly.length));

				g.setColor(border);

				g.drawPolygon(new Polygon(xPoly, yPoly, xPoly.length));

				g.setColor(font);
				g.drawString("P", size + dist + 3, 12);

				// bow
				setColor(g, hullIntegrity.getBow());
				g.fillArc(size * 2 + dist * 2, 0, size, size, 0, 360);
				g.setColor(border);
				g.drawArc(size * 2 + dist * 2, 0, size, size, 0, 360);
				g.setColor(font);
				g.drawString("B", size * 2 + dist * 2 + 3, 11);

			}
		}

		private void setColor(Graphics g, Status stern) {
			if (stern == Status.OK) {
				g.setColor(okColor);
			} else if (stern == Status.BROKEN) {
				g.setColor(brokenColor);
			} else if (stern == Status.PROBLEMATIC) {
				g.setColor(problemColor);
			}
		}

	}

}
