package de.oliverprobst.tdk.navi.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oliverprobst.tdk.navi.App;
import de.oliverprobst.tdk.navi.GeoCalculator;
import de.oliverprobst.tdk.navi.NmeaParser;
import de.oliverprobst.tdk.navi.config.Waypoint;
import de.oliverprobst.tdk.navi.controller.DiveDataProperties;
import de.oliverprobst.tdk.navi.dto.Location;
import de.oliverprobst.tdk.navi.dto.StructuralIntegrity;
import de.oliverprobst.tdk.navi.dto.StructuralIntegrity.Status;

/**
 * Paint the main map panel, including current position, course and warnings.
 * 
 * Image dimension: 480, 360
 * 
 * Note: I do have the strange feeling, that some of the methods of this class
 * can do much more efficient when using a better AWT API.
 * 
 * Maybe this should be considered within the next reengineering tasks.
 */
public class MapPanel extends AbstractNaviJPanel implements
		PropertyChangeListener {

	private static Logger log = LoggerFactory.getLogger(MapPanel.class);
	private static final long serialVersionUID = -1775948240481194745L;

	private Polygon arrowHead = new Polygon();

	private BufferedImage image;

	private int lastCourse = 0;

	private double lastLatitude = 0;

	private double lastLongitude = 0;
	ArrayList<MapPoint> locations = new ArrayList<MapPoint>();

	private double minDop = 2.5;
	private final MapPanelColors panelColorScheme;

	AffineTransform tx = new AffineTransform();

	/**
	 * Will be set to true when a new location was provided. And set to false,
	 * if waypoints were updated. This prevents unnecessary updates of the WPs.
	 */
	private boolean updateWPs = false;

	/** A warning displayed on top of the map. */
	private String warning = null;

	/**
	 * The warn prio prevents more important user messages to be overwritten by
	 * less. Eg. Warning voltage shall not overwrite leak detection.
	 */
	private int warnPrio = 0;

	/**
	 * Cache all calculated bearings and refresh list only on location change.
	 * #Performance
	 */
	private final List<Integer> wpBearingCache;

	/**
	 * Cache all calculated distances and refresh list only on location change.
	 * #Performance
	 */
	private final List<Integer> wpDistanceCache;

	/** All waypoints on the map. */
	private final Collection<Waypoint> wps;

	public MapPanel(Collection<Waypoint> wps, String imageLocation,
			boolean brightColorRoute) {
		this.wps = wps;
		wpBearingCache = new ArrayList<>(wps.size());
		wpDistanceCache = new ArrayList<>(wps.size());
		// fill with initial undefined values
		for (int i = 0; i < wps.size(); i++) {
			wpDistanceCache.add(-1);
			wpBearingCache.add(-1);
		}
		panelColorScheme = new MapPanelColors(brightColorRoute);
		final String internPrefix = "${intern}";
		InputStream is = null;

		// delivered with jar:
		if (imageLocation.trim().startsWith(internPrefix)) {
			imageLocation = imageLocation.trim().replace(internPrefix, "");
			log.info("Loading internal map " + imageLocation);
			is = MapPanel.class.getResourceAsStream(imageLocation);
		} else {
			// loaded from file system
			imageLocation = imageLocation.replaceAll("\\$\\{user.home\\}",
					System.getProperty("user.home"));
			File file = new File(imageLocation);
			log.info("Loading external map " + imageLocation);
			try {
				is = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Error loading file "
						+ file.getAbsolutePath());

			}

		}

		arrowHead.addPoint(0, 5);
		arrowHead.addPoint(-5, -5);
		arrowHead.addPoint(5, -5);

		try {
			image = ImageIO.read(is);
		} catch (IOException ex) {
			throw new RuntimeException("Error loading demo map 'demoMap.png'.",
					ex);
		}
		this.setPreferredSize(new Dimension(480, 360));

		if (App.getConfig().getSettings().getMinGpsQuality() != null) {
			minDop = App.getConfig().getSettings().getMinGpsQuality();
		}

	}

	private void drawArrowHead(Graphics2D g2d, Line2D.Double line) {
		tx.setToIdentity();
		double angle = Math.atan2(line.y2 - line.y1, line.x2 - line.x1);
		tx.translate(line.x2, line.y2);
		tx.rotate((angle - Math.PI / 2d));

		Graphics2D g = (Graphics2D) g2d.create();
		g.setTransform(tx);
		g.fill(arrowHead);
		g.dispose();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		g2d.addRenderingHints(defineRenderingHints());

		g2d.drawImage(image, 0, 0, null);

		MapPoint lastLocation = null;

		paintRoute(g2d, lastLocation);

		if (!locations.isEmpty()) {
			lastLocation = locations.get(locations.size() - 1);
			paintNavArrow(g2d, lastLocation.x, lastLocation.y);
		}
		paintWPs(g2d);

		if (warning != null) {
			g2d.setColor(new Color(255, 1, 1));
			g2d.setFont(new Font("Dialog", Font.BOLD, 18));
			g2d.drawString(warning, 15, 35);
		} 
		if (entertainmentRunning) {
			g2d.setColor(new Color(20, 200, 20));
			g2d.setFont(new Font("Dialog", Font.BOLD, 18));
			g2d.drawString("Starting Entertainment Mode. Enjoy deco!", 15, 300);
		}
	}

	/**
	 * Paint the current location and the course arrow
	 *
	 * @param g
	 *            the graphics object
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 */
	private void paintNavArrow(Graphics2D g, int x, int y) {

		final int size = 8;
		// location

		// White cycle
		g.setColor(new Color(255, 255, 255));
		g.drawArc(x - (size - 1), y - (size - 1), size * 2 - 2, size * 2 - 2,
				0, 360);
		g.drawArc(x - 2, y - 2, 4, 4, 0, 360);
		// Black cycle
		g.setColor(new Color(0, 0, 0));
		g.drawArc(x - size, y - size, size * 2, size * 2, 0, 360);
		g.drawArc(x - 1, y - 1, 2, 2, 0, 360);

		// line for compass course and arrow
		g.setColor(new Color(255, 0, 0));

		final float scaleFactor = .25f;
		float rC = (float) (lastCourse + 0.5f);
		float offX = 0;
		float offY = 0;

		if (rC >= 0 && rC <= 90) {
			offY = rC - 90;
			offX = rC;
		} else if (rC >= 90 && rC < 180) {
			offY = rC - 90;
			offX = 90 - (rC - 90);
		} else if (rC >= 180 && rC < 270) {
			offY = (rC - 270) * -1;
			offX = (rC - 180) * -1;
		} else if (rC >= 270 && rC <= 361) {
			offY = (rC - 270) * -1;
			offX = -90 + (rC - 270);
		}

		offX = offX * scaleFactor;
		offY = offY * scaleFactor;

		Line2D.Double line = new Line2D.Double(x, y, x + (int) offX, y
				+ (int) offY);

		drawArrowHead(g, line);

	}

	private void paintRoute(Graphics2D g2d, MapPoint lastLocation) {
		int stepSize = (int) (((double) (locations.size() + 26) / 50) + 0.5);
		for (int i = 0; i < locations.size(); i += stepSize) {
			MapPoint location = locations.get(i);

			// last 10 records in red:
			boolean newerOne = (i > locations.size() - 11 * stepSize);

			g2d.setColor(panelColorScheme.getRouteColor(location.isEstimated(),
					newerOne));

			if (lastLocation != null) {
				g2d.drawLine(lastLocation.x, lastLocation.y, location.x,
						location.y);
				g2d.drawLine(lastLocation.x + 1, lastLocation.y + 1,
						location.x + 1, location.y + 1);
			}
			lastLocation = location;
			if (i > locations.size() - 50) {
				stepSize = 1;
			}
		}
	}

	private void paintWPs(Graphics2D g) {
		Dimension d = new Dimension(image.getWidth(), image.getHeight());
		GeoCalculator hc = GeoCalculator.getInstance();
		int iterC = -1;
		for (Waypoint wp : wps) {
			iterC++;
			MapPoint loc = hc.xyProjection(d, wp.getLongitude(),
					wp.getLatitude());

			g.setColor(new Color(180, 180, 255));
			g.drawArc(loc.x, loc.y, 4, 4, 0, 360);
			g.setColor(new Color(0, 0, 255));
			final int size = 3;
			g.drawLine(loc.x - size, loc.y - size, loc.x + size, loc.y + size);
			g.drawLine(loc.x - size, loc.y + size, loc.x + size, loc.y - size);

			g.setColor(new Color(0, 0, 255));
			g.setFont(new Font("Arial", Font.BOLD, 12));
			g.drawString(wp.getId(), loc.x + 8, loc.y);

			int distance = -1;
			int bearing = -1;
			if (updateWPs) {

				if (lastLongitude != 0) {
					distance = hc.calculateDistance(lastLatitude,
							lastLongitude, wp.getLatitude(), wp.getLongitude());
					bearing = hc.calculateBearing(lastLatitude, lastLongitude,
							wp.getLatitude(), wp.getLongitude());
				}

				wpBearingCache.set(iterC, bearing);
				wpDistanceCache.set(iterC, distance);
			} else {
				bearing = wpBearingCache.get(iterC);
				distance = wpDistanceCache.get(iterC);
			}

			if (bearing >= 0) {
				g.drawString(bearing + " °", loc.x + 8, loc.y + 12);
			}
			if (distance >= 0) {
				g.drawString(distance + " m", loc.x + 8, loc.y + 24);
			}
		}
		updateWPs = false;
	}

	boolean entertainmentRunning = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.
	 * PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		if (evt.getPropertyName().equals(DiveDataProperties.PROP_GPSFIX)) {
			setGPSLocation((NmeaParser) evt.getNewValue());
			this.update();
		} else if (evt.getPropertyName().equals(
				DiveDataProperties.PROP_ESTIMATED)) {
			setEstimatedLocation((Location) evt.getNewValue());
			this.update();
		} else if (evt.getPropertyName().equals(
				DiveDataProperties.PROP_ENTERTAINMENTSTARTED)) {
			entertainmentRunning = (boolean) evt.getNewValue();
			this.update();
		} else if (evt.getPropertyName().equals(DiveDataProperties.PROP_COURSE)) {
			int newCourse = (Integer) evt.getNewValue();
			if (lastCourse == newCourse) {
				// only if new bearing is different!
				return;
			}
			lastCourse = newCourse;
			this.update();
		} else if (evt.getPropertyName()
				.equals(DiveDataProperties.PROP_VOLTAGE)) {
			float voltage = (float) evt.getNewValue();
			String voltString = new DecimalFormat("0.0").format(voltage);
			if (voltage < App.getConfig().getSettings().getWarningVoltage()
					&& warnPrio <= 1) {
				warning = "Voltage " + voltString + "V - shutdown at "
						+ App.getConfig().getSettings().getShutdownVoltage()
						+ "V.";
				warnPrio = 1;
			}
			if (voltage < App.getConfig().getSettings().getShutdownVoltage()) {
				warning = "Low Voltage (" + voltString
						+ "V) - system shutdown!";
			}
			this.update();
		} else if (evt.getPropertyName().equals(DiveDataProperties.PROP_HULL)) {
			StructuralIntegrity si = (StructuralIntegrity) evt.getNewValue();
			warning = null;

			if (si.getAmbient() == Status.BROKEN
					|| si.getStern() == Status.BROKEN
					|| si.getBow() == Status.BROKEN) {
				warning = "LEAK WARNING: Shutdown initiated.";
				warnPrio = 3;
				this.update();
			}

		} else if (evt.getPropertyName().equals(
				DiveDataProperties.PROP_SHUTDOWN)) {
			String result = (String) evt.getNewValue();
			if (result.equals("1") && warnPrio <= 2) {
				warning = "Turning system off. Good Bye!";

				warnPrio = 2;
				this.update();
			}
		}

	}

	/**
	 * 
	 * TODO This could be a bit nicer, it is more or less a duplicate of the
	 * other drawLocation
	 * 
	 * @param newValue
	 * @param b
	 */
	private void setEstimatedLocation(Location newValue) {
		if (newValue != null) {
			Dimension d = new Dimension(image.getWidth(), image.getHeight());
			GeoCalculator hc = GeoCalculator.getInstance();
			lastLatitude = newValue.getLatitude();
			lastLongitude = newValue.getLongitude();
			MapPoint location = hc.xyProjection(d, lastLongitude, lastLatitude);
			location.setEstimated(true);
			if (!locations.get(locations.size() - 1).equals(location)
					&& location.x > 0 && location.y > 0 && location.x < 480
					&& location.y < 360) {
				locations.add(location);
				updateWPs = true;
			}
		}

	}

	private void setGPSLocation(NmeaParser parser) {

		if (parser != null && parser.isValid()) {
			Dimension d = new Dimension(image.getWidth(), image.getHeight());
			GeoCalculator hc = GeoCalculator.getInstance();

			lastLatitude = parser.getLatitude();
			lastLongitude = parser.getLongitude();

			MapPoint location = hc.xyProjection(d, parser.getLongitude(),
					parser.getLatitude());
			if (parser.getDiluentOfPrecision() <= minDop) {
				if ((locations.isEmpty() || !locations
						.get(locations.size() - 1).equals(location))
						&& location.x > 0
						&& location.y > 0
						&& location.x < 480
						&& location.y < 360) {
					locations.add(location);
					updateWPs = true;
				}
			}
		}
	}

	private void update() {
		this.updateUI();
		this.repaint();

	}
}
