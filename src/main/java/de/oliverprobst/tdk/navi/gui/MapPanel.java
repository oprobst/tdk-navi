package de.oliverprobst.tdk.navi.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import de.oliverprobst.tdk.navi.HaversineConverter;
import de.oliverprobst.tdk.navi.NmeaParser;
import de.oliverprobst.tdk.navi.controller.DiveDataProperties;

/**
 * 
 * Image dimension: 480, 360
 */
public class MapPanel extends JPanel implements PropertyChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1775948240481194745L;

	private BufferedImage image;
	int lastCourse = 0;
	ArrayList<Point> locations = new ArrayList<Point>();

	public MapPanel() {

		ClassLoader classloader = Thread.currentThread()
				.getContextClassLoader();
		InputStream is = classloader.getResourceAsStream("demoMap.png");

		try {
			image = ImageIO.read(is);
		} catch (IOException ex) {
			throw new RuntimeException("Error loading demo map 'demoMap.png'.",
					ex);
		}
		this.setPreferredSize(new Dimension(480, 360));

	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 0, 0, null);

		g.setColor(new Color(255, 100, 100));
		Point lastLocation = null;

		int stepSize = (int) (((double) (locations.size() + 26) / 50) + 0.5);
		for (int i = 0; i < locations.size(); i += stepSize) {
			Point location = locations.get(i);

			// last 10 records in red:
			if (i > locations.size() - 11 * stepSize) {
				g.setColor(new Color(255, 50, 50));
			}
			// last 3 records in dark red:
			if (i > locations.size() - 4 * stepSize) {
				g.setColor(new Color(255, 000, 000));
			}
			if (lastLocation != null) {
				g.drawLine(lastLocation.x, lastLocation.y, location.x,
						location.y);
			}
			if (i == locations.size() - 1) {
				drawArrow(g, location.x, location.y);
			}

			lastLocation = location;
		}
	}

	private void drawArrow(Graphics g, int x, int y) {

		int offX = 0;
		int offY = 0;

		if (lastCourse > 336 || lastCourse < 22) {
			offY = -8;
			offX = 0;
		} else if (lastCourse > 22 && lastCourse < 67) {
			offY = -5;
			offX = +5;
		} else if (lastCourse > 66 && lastCourse < 112) {
			offY = 0;
			offX = +8;
		} else if (lastCourse > 111 && lastCourse < 157) {
			offY = +5;
			offX = +5;
		} else if (lastCourse > 156 && lastCourse < 202) {
			offY = +8;
			offX = +0;
		} else if (lastCourse > 201 && lastCourse < 247) {
			offY = +5;
			offX = -5;
		} else if (lastCourse > 246 && lastCourse < 292) {
			offY = 0;
			offX = -8;
		} else if (lastCourse > 291 && lastCourse < 337) {
			offY = -5;
			offX = -5;
		}
		final int size = 4;
		g.drawLine(x - size, y - size, x + size, y + size);
		g.drawLine(x - size, y + size, x + size, y - size);
		g.setColor(new Color(0, 0, 255));
		g.drawLine(x, y, x + offX, y + offY);

	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(DiveDataProperties.PROP_GPSFIX)) {
			drawLocation(evt.getNewValue());
		}
		if (evt.getPropertyName().equals(DiveDataProperties.PROP_COURSE)) {
			lastCourse = (Integer) evt.getNewValue();
			drawLocation(null);
		}

	}

	private void drawLocation(Object newValue) {

		if (newValue != null) {
			Dimension d = new Dimension(image.getWidth(), image.getHeight());
			HaversineConverter hc = HaversineConverter.getInstance();
			NmeaParser p = new NmeaParser((String) newValue);
			Point location = hc.xyProjection(d, p.getLatitude(),
					p.getLongitude());

			locations.add(location);

		}
		this.updateUI();
		this.repaint();

	}

}
