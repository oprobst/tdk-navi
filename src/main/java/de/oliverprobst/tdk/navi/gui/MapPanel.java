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

		g.setColor(new Color(255, 200, 200));
		Point lastLocation = null;

		int stepSize = (int) (locations.size() + 26 / 50);

		for (int i = 0; i < locations.size(); i += stepSize) {
			Point location = locations.get(i);

			// last 10 records in red:
			if (i > locations.size() - 11 * stepSize) {
				g.setColor(new Color(255, 100, 100));
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
				g.drawLine(location.x - 1, location.y - 1, location.x + 1,
						location.y + 1);
				g.drawLine(location.x + 1, location.y - 1, location.x + 1,
						location.y - 1);
			}

			lastLocation = location;
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(DiveDataProperties.PROP_GPSFIX)) {
			drawLocation(evt.getNewValue());
		}

	}

	ArrayList<Point> locations = new ArrayList<Point>();

	private void drawLocation(Object newValue) {
		Dimension d = new Dimension(image.getWidth(), image.getHeight());
		HaversineConverter hc = HaversineConverter.getInstance();
		NmeaParser p = new NmeaParser((String) newValue);
		Point location = hc.xyProjection(d, p.getLongitude(), p.getLatitude());

		locations.add(location);
		this.updateUI();

	}

}
