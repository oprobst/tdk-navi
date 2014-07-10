package de.oliverprobst.tdk.navi.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import de.oliverprobst.tdk.navi.HaversineConverter;
import de.oliverprobst.tdk.navi.NmeaParser;
import de.oliverprobst.tdk.navi.controller.DiveDataProperties;
import de.oliverprobst.tdk.navi.dto.StructuralIntegrity;
import de.oliverprobst.tdk.navi.dto.StructuralIntegrity.Status;
import de.oliverprobst.tdk.navi.dto.Waypoint;

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

	public MapPanel(Collection<Waypoint> wps) {
		this.wps = wps;

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

		if (locations.isEmpty()) {
			return;
		}
		g.setColor(new Color(255, 150, 150));
		Point lastLocation = null;

		int stepSize = (int) (((double) (locations.size() + 26) / 50) + 0.5);
		for (int i = 0; i < locations.size(); i += stepSize) {
			Point location = locations.get(i);

			// last 10 records in red:
			if (i > locations.size() - 11 * stepSize) {
				g.setColor(new Color(255, 100, 100));
			}
			// last 3 records in dark red:
			if (i > locations.size() - 4 * stepSize) {
				g.setColor(new Color(255, 50, 00));
			}
			if (lastLocation != null) {
				g.drawLine(lastLocation.x, lastLocation.y, location.x,
						location.y);
			}
			lastLocation = location;
			if (i > locations.size() - 50){
				stepSize = 1;
			}
		}		
		lastLocation = locations.get(locations.size() - 1);
		drawArrow(g, lastLocation.x, lastLocation.y);

		drawWPs(g);
	}

	private final Collection<Waypoint> wps;

	private void drawWPs(Graphics g) {
		Dimension d = new Dimension(image.getWidth(), image.getHeight());
		HaversineConverter hc = HaversineConverter.getInstance();

		for (Waypoint wp : wps) {
			Point loc = hc.xyProjection(d, wp.getLongitude(), wp.getLatitude());

			int distance = -1;
			int bearing = -1;
			if (lastLongitude != 0) {
				distance = hc.calculateDistance(lastLatitude, lastLongitude,
						 wp.getLatitude(), wp.getLongitude());
				bearing = hc.calculateBearing(lastLatitude, lastLongitude,
						wp.getLatitude(), wp.getLongitude());
			}

			g.setColor(new Color(180, 180, 255));
			g.drawArc(loc.x, loc.y, 4, 4, 0, 360);
			g.setColor(new Color(0, 0, 255));
			final int size = 3;
			g.drawLine(loc.x - size, loc.y - size, loc.x + size, loc.y + size);
			g.drawLine(loc.x - size, loc.y + size, loc.x + size, loc.y - size);

			g.setColor(new Color(0, 0, 255));
			g.setFont(new Font("Dialog", Font.BOLD, 10));
			g.drawString(wp.getName(), loc.x + 6, loc.y);
			if (bearing >= 0) {
				g.drawString(bearing + " °", loc.x + 6, loc.y + 10);
			}
			if (distance >= 0) {
				g.drawString(distance + " m", loc.x + 6, loc.y + 20);
			}
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
		// arc
		g.setColor(new Color(255, 200, 200));
		g.drawArc(x - size, y - size, size * 2, size * 2, 0, 360);
		// line
		g.setColor(new Color(255, 0, 0));
		g.drawLine(x - size, y - size, x + size, y + size);
		g.drawLine(x - size, y + size, x + size, y - size);
		// direction
		g.setColor(new Color(0, 0, 255));
		g.drawLine(x, y, x + offX, y + offY);
		g.drawArc(x + offX - 1, y + offY - 1, 2, 2, 0, 360);

		if (warning != null) {
			g.setColor(new Color(255, 1, 1));
			g.setFont(new Font("Dialog", Font.BOLD, 18));
			g.drawString(warning, 15, 35);
		}

		// NavPoints

	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(DiveDataProperties.PROP_GPSFIX)) {
			drawLocation(evt.getNewValue());

		}
		if (evt.getPropertyName().equals(DiveDataProperties.PROP_COURSE)) {
			lastCourse = (Integer) evt.getNewValue();
			drawLocation(null);
		}
		if (evt.getPropertyName().equals(DiveDataProperties.PROP_HULL)) {
			StructuralIntegrity si = (StructuralIntegrity) evt.getNewValue();
			warning = null;

			if (si.getAmbient() == Status.BROKEN
					|| si.getStern() == Status.BROKEN
					|| si.getBow() == Status.BROKEN) {
				warning = "WARNING: POSSIBLE LEAK DETECTED!";
			}
			drawLocation(null);
		}

	}

	private String warning = null;

	private double lastLongitude = 0;
	private double lastLatitude = 0;

	private void drawLocation(Object newValue) {

		if (newValue != null) {
			Dimension d = new Dimension(image.getWidth(), image.getHeight());
			HaversineConverter hc = HaversineConverter.getInstance();
			NmeaParser p = new NmeaParser((String) newValue);
			lastLatitude = p.getLatitude();
			lastLongitude = p.getLongitude();
			Point location = hc.xyProjection(d, p.getLongitude(),
					p.getLatitude());

			locations.add(location);

		}
		this.updateUI();
		this.repaint();

	}

}
