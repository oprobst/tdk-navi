package de.oliverprobst.tdk.navi.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oliverprobst.tdk.navi.controller.DefaultController;
import de.oliverprobst.tdk.navi.dto.DiveData;
import de.oliverprobst.tdk.navi.dto.StructuralIntegrity.Status;

public class MainDialog extends JFrame {

	private static Logger log = LoggerFactory.getLogger(MainDialog.class);
	/**
	 * sid
	 */
	private static final long serialVersionUID = -2892164373355188397L;

	private final DefaultController dc;
	protected Layouter layouter = new Layouter();
	private Insets defInsets = new Insets(0, 0, 0, 0);

	/**
	 * ctor
	 */
	public MainDialog(DefaultController dc) {
		super();
		this.dc = dc;
		this.setSize(640, 480);
		this.setLocation(100, 100);
		this.layouter.layout(this.getContentPane());
		this.setResizable(false);
		this.setUndecorated(true);
		this.setVisible(true);
		createMainGridLayout();
		registerKeyListener();
	}

	private void createMainGridLayout() {
		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);

		GridBagConstraints gbc = new GridBagConstraints(0, 0, 2, 1, 1.0d, 0.0d,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 2, 2);

		JPanel topPanel = new JPanel();
		layouter.layout(topPanel);
		gbl.setConstraints(topPanel, gbc);
		this.add(topPanel, gbc);

		gbc = new GridBagConstraints(0, 1, 1, 1, 1.0d, 1.0d,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 1, 1);

		JPanel mapPanel = new JPanel();
		layouter.layout(mapPanel);
		gbl.setConstraints(mapPanel, gbc);
		this.add(mapPanel, gbc);

		gbc = new GridBagConstraints(1, 1, 1, 1, 0.0d, 0.8d,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 1, 1);

		JPanel paramPanel = new JPanel();
		layouter.layout(paramPanel);
		gbl.setConstraints(paramPanel, gbc);
		this.add(paramPanel, gbc);

		gbc = new GridBagConstraints(0, 2, 2, 1, 0.0d, 0.15d,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 2, 2);

		JPanel bottomPanel = new JPanel();
		layouter.layout(bottomPanel);
		gbl.setConstraints(bottomPanel, gbc);
		this.add(bottomPanel, gbc);

		createTopPanel(topPanel);
		createMapPanel(mapPanel);
		createParamPanel(paramPanel);
		createBottomPanel(bottomPanel);

	}

	private void createBottomPanel(JPanel panel) {
		GridBagLayout gbl = new GridBagLayout();
		panel.setLayout(gbl);

		GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 3, 1.0d, 1.0d,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, defInsets,
				2, 2);

		DiveProfilePanel profilePanel = new DiveProfilePanel(layouter);

		gbl.setConstraints(profilePanel, gbc);
		panel.add(profilePanel, gbc);
		dc.registerControllerPropertyChangeListener(profilePanel);

		gbc = new GridBagConstraints(1, 0, 1, 1, 0.0d, 0.0d,
				GridBagConstraints.EAST, GridBagConstraints.BOTH, defInsets, 2,
				2);

		VoltagePanel voltagePanel = new VoltagePanel(layouter);

		gbl.setConstraints(voltagePanel, gbc);
		panel.add(voltagePanel, gbc);
		dc.registerModelPropertyListener(voltagePanel);

		gbc = new GridBagConstraints(1, 1, 1, 1, 0.0d, 0.0d,
				GridBagConstraints.EAST, GridBagConstraints.BOTH, defInsets, 2,
				2);

		HumidityPanel humidityPanel = new HumidityPanel(layouter);

		gbl.setConstraints(humidityPanel, gbc);
		panel.add(humidityPanel, gbc);
		dc.registerModelPropertyListener(humidityPanel);

		gbc = new GridBagConstraints(1, 2, 1, 1, 0.0d, 0.0d,
				GridBagConstraints.EAST, GridBagConstraints.BOTH, defInsets, 2,
				2);

		StructureIntegrityPanel structureIntegrity = new StructureIntegrityPanel(
				layouter);

		gbl.setConstraints(structureIntegrity, gbc);
		panel.add(structureIntegrity, gbc);
		dc.registerModelPropertyListener(structureIntegrity);

	}

	private void createParamPanel(JPanel panel) {
		GridBagLayout gbl = new GridBagLayout();
		panel.setLayout(gbl);

		GridBagConstraints gbc = new GridBagConstraints(0, 0, 2, 1, 1.0d, 0.2d,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, defInsets,
				2, 2);

		GpsCoordPanel gpsPanel = new GpsCoordPanel(layouter);

		gbl.setConstraints(gpsPanel, gbc);
		panel.add(gpsPanel, gbc);
		dc.registerModelPropertyListener(gpsPanel);

		gbc = new GridBagConstraints(0, 1, 1, 1, 1.0d, 1.0d,
				GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
				defInsets, 2, 2);

		MaxDepthPanel maxDepthPanel = new MaxDepthPanel(layouter);

		gbl.setConstraints(maxDepthPanel, gbc);
		panel.add(maxDepthPanel, gbc);
		dc.registerModelPropertyListener(maxDepthPanel);

		gbc = new GridBagConstraints(1, 1, 1, 1, 1.0d, 1.0d,
				GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
				defInsets, 2, 2);

		LastFixPanel lastFixPanel = new LastFixPanel(layouter);

		gbl.setConstraints(lastFixPanel, gbc);
		panel.add(lastFixPanel, gbc);
		dc.registerModelPropertyListener(lastFixPanel);

		gbc = new GridBagConstraints(0, 2, 1, 1, 1.0d, 1.0d,
				GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
				defInsets, 2, 2);

		TemperaturePanel temperaturePanel = new TemperaturePanel(layouter);

		gbl.setConstraints(temperaturePanel, gbc);
		panel.add(temperaturePanel, gbc);
		dc.registerModelPropertyListener(temperaturePanel);

		gbc = new GridBagConstraints(1, 2, 1, 1, 1.0d, 1.0d,
				GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
				defInsets, 2, 2);

		AvgDiveDepthPanel averageDepthPanel = new AvgDiveDepthPanel(layouter);

		gbl.setConstraints(averageDepthPanel, gbc);
		panel.add(averageDepthPanel, gbc);
		dc.registerModelPropertyListener(averageDepthPanel);

		gbc = new GridBagConstraints(0, 3, 1, 1, 0.0d, 0.0d,
				GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
				defInsets, 2, 2);

		SpeedPanel speedPanel = new SpeedPanel(layouter);

		gbl.setConstraints(speedPanel, gbc);
		panel.add(speedPanel, gbc);
		dc.registerModelPropertyListener(speedPanel);

		gbc = new GridBagConstraints(1, 3, 1, 1, 0.0d, 0.0d,
				GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
				defInsets, 2, 2);

		PitchPanel pitchPanel = new PitchPanel(layouter);

		gbl.setConstraints(pitchPanel, gbc);
		panel.add(pitchPanel, gbc);
		dc.registerModelPropertyListener(pitchPanel);

	}

	private void createMapPanel(JPanel panel) {

		GridBagLayout gbl = new GridBagLayout();
		panel.setLayout(gbl);

		GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1.0d, 1.0d,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, defInsets,
				0, 0);
		MapPanel mapPanel = new MapPanel();
		panel.add(mapPanel, gbc);
		dc.registerModelPropertyListener(mapPanel);

		layouter.layout(mapPanel);
	}

	private void createTopPanel(JPanel panel) {
		GridBagLayout gbl = new GridBagLayout();
		panel.setLayout(gbl);

		GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 0.25d,
				1.0d, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				defInsets, 2, 2);

		DepthPanel depthPanel = new DepthPanel(layouter);

		gbl.setConstraints(depthPanel, gbc);
		panel.add(depthPanel, gbc);
		dc.registerModelPropertyListener(depthPanel);

		gbc = new GridBagConstraints(1, 0, 1, 1, 0.5d, 1.0d,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, defInsets,
				15, 2);

		CompassPanel compassPanel = new CompassPanel(layouter);

		gbl.setConstraints(compassPanel, gbc);
		panel.add(compassPanel, gbc);
		dc.registerModelPropertyListener(compassPanel);

		gbc = new GridBagConstraints(2, 0, 1, 1, 0.25d, 1.0d,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, defInsets,
				15, 2);

		DiveTimePanel diveTimePanel = new DiveTimePanel(layouter);

		gbl.setConstraints(diveTimePanel, gbc);
		panel.add(diveTimePanel, gbc);
		dc.registerModelPropertyListener(diveTimePanel);

	}

	/**
	 * React on user input
	 */
	private void registerKeyListener() {
		this.addKeyListener(new KeyListener() {

			public void keyTyped(KeyEvent e) {
			}

			public void keyPressed(KeyEvent e) {

				DiveData dd = dc.getCurrentRecordClone();

				switch (e.getKeyCode()) {
				case 81: // q

					log.info("User input was 'Q', which means I've to leave... Good bye!");
					System.exit(0);
					break;
				case 87: // w
				case 38: // arr up
					float depth = dd.getDepth();
					depth -= .25;
					if (depth < 0) {
						depth = 0;
					}
					dc.setDepth(depth);
					break;
				case 83: // s
				case 40: // arr down
					depth = dd.getDepth();
					depth += .25;
					dc.setDepth(depth);
					break;

				case 69: // e
				case 39: // arr right
					int course = dd.getCourse();
					course += 2;
					if (course > 359) {
						course = 0;
					}
					dc.setCourse(course);
					break;
				case 68: // d
				case 37: // arr left
					course = dd.getCourse();
					course -= 2;
					if (course < 0) {
						course = 359;
					}
					dc.setCourse(course);
					break;
				case 32: // space
				case 82: // r
					SimpleDateFormat dateFormatGmt = new SimpleDateFormat(
							"HH:mm:ss.00");
					dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
					String message = "$GPGGA,161718.53,4846.13423,N,00819.92680,E,1,05,1.63,190.9,M,47.4,M,,*59";
					// (checksum invalid)
					dc.setGGA(message);

					break;
				case 107: // +
					int speed = dd.getSpeed();
					speed += 1;
					if (speed > 10) {
						speed = 10;
					}
					dc.setSpeed(speed);
					break;
				case 109: // -
					speed = dd.getSpeed();
					speed -= 1;
					if (speed < 0) {
						speed = 0;
					}
					dc.setSpeed(speed);
					break;
				case 34:// pageDown
					int pitch = dd.getPitch();
					pitch -= 1;
					if (pitch < -180) {
						pitch = +180;
					}
					dc.setPitch(pitch);
					break;
				case 33: // pageUp
					pitch = dd.getPitch();
					pitch += 1;
					if (pitch > 180) {
						pitch = -180;
					}
					dc.setPitch(pitch);
					break;

				case 71: // t
					if (dd.getIntegrity().getLastCode() >= 100000) {
						dc.setIntegrityCode(dd.getIntegrity().getLastCode() - 100000);
					} else {
						dc.setIntegrityCode(dd.getIntegrity().getLastCode() + 100000);
					}
					break;
				case 84: // g
					if (dd.getIntegrity().getLastCode() - 100000 >= 10000
							|| ((dd.getIntegrity().getLastCode() < 100000) && (dd
									.getIntegrity().getLastCode() >= 10000))) {
						dc.setIntegrityCode(dd.getIntegrity().getLastCode() - 10000);
					} else {
						dc.setIntegrityCode(dd.getIntegrity().getLastCode() + 10000);
					}
					break;
				case 90: // z
					dc.setIntegrityCode(dd.getIntegrity().getLastCode() + 100);
					break;
				case 72: // h
					dc.setIntegrityCode(dd.getIntegrity().getLastCode() - 100);
					break;
				case 70: // f
					dc.setIntegrityCode(1013);
					dd.getIntegrity().setBow(Status.OK);
					dd.getIntegrity().setAmbient(Status.OK);
					dd.getIntegrity().setStern(Status.OK);
					break;

				default:
					// nada
					log.debug("Unknown key: " + e.getKeyCode());
				}

			}

			public void keyReleased(KeyEvent e) {
			}
		});

	}
}
