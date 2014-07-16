package de.oliverprobst.tdk.navi.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oliverprobst.tdk.navi.controller.DefaultController;

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
	 * 
	 * @param config
	 */
	public MainDialog(DefaultController dc) {
		super();
		this.dc = dc;
		this.setSize(640, 480);
		this.setLocation(0, 000);
		this.layouter.layout(this.getContentPane());
		this.setResizable(false);
		this.setUndecorated(true);
		this.setVisible(true);
		createMainGridLayout();
		this.addKeyListener(new DemoKeyListener(dc));
	}

	private void createMainGridLayout() {
		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);

		GridBagConstraints gbc = new GridBagConstraints(0, 0, 2, 1, 1.0d, 0.0d,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 1);

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
						0, 0, 0, 0), 2, 1);

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
				0, 0);

		DiveProfilePanel profilePanel = new DiveProfilePanel(layouter);

		gbl.setConstraints(profilePanel, gbc);
		panel.add(profilePanel, gbc);
		dc.registerControllerPropertyChangeListener(profilePanel);

		gbc = new GridBagConstraints(1, 0, 1, 1, 0.0d, 0.0d,
				GridBagConstraints.EAST, GridBagConstraints.BOTH, defInsets, 0,
				0);

		VoltagePanel voltagePanel = new VoltagePanel(layouter);

		gbl.setConstraints(voltagePanel, gbc);
		panel.add(voltagePanel, gbc);
		dc.registerModelPropertyListener(voltagePanel);

		gbc = new GridBagConstraints(1, 1, 1, 1, 0.0d, 0.0d,
				GridBagConstraints.EAST, GridBagConstraints.BOTH, defInsets, 0,
				0);

		HumidityPanel humidityPanel = new HumidityPanel(layouter);

		gbl.setConstraints(humidityPanel, gbc);
		panel.add(humidityPanel, gbc);
		dc.registerModelPropertyListener(humidityPanel);

		gbc = new GridBagConstraints(1, 2, 1, 1, 0.0d, 0.0d,
				GridBagConstraints.EAST, GridBagConstraints.BOTH, defInsets, 0,
				0);

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
				0, 0);

		GpsCoordPanel gpsPanel = new GpsCoordPanel(layouter);

		gbl.setConstraints(gpsPanel, gbc);
		panel.add(gpsPanel, gbc);
		dc.registerModelPropertyListener(gpsPanel);

		gbc = new GridBagConstraints(0, 1, 1, 1, 1.0d, 1.0d,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, defInsets,
				0, 0);

		MaxDepthPanel maxDepthPanel = new MaxDepthPanel(layouter);

		gbl.setConstraints(maxDepthPanel, gbc);
		panel.add(maxDepthPanel, gbc);
		dc.registerModelPropertyListener(maxDepthPanel);

		gbc = new GridBagConstraints(1, 1, 1, 1, 1.0d, 1.0d,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, defInsets,
				0, 0);

		LastFixPanel lastFixPanel = new LastFixPanel(layouter);

		gbl.setConstraints(lastFixPanel, gbc);
		panel.add(lastFixPanel, gbc);
		dc.registerModelPropertyListener(lastFixPanel);

		gbc = new GridBagConstraints(0, 2, 1, 1, 1.0d, 1.0d,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, defInsets,
				0, 0);

		TemperaturePanel temperaturePanel = new TemperaturePanel(layouter);

		gbl.setConstraints(temperaturePanel, gbc);
		panel.add(temperaturePanel, gbc);
		dc.registerModelPropertyListener(temperaturePanel);

		gbc = new GridBagConstraints(1, 2, 1, 1, 1.0d, 1.0d,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, defInsets,
				0, 0);

		AvgDiveDepthPanel averageDepthPanel = new AvgDiveDepthPanel(layouter);

		gbl.setConstraints(averageDepthPanel, gbc);
		panel.add(averageDepthPanel, gbc);
		dc.registerModelPropertyListener(averageDepthPanel);

		gbc = new GridBagConstraints(0, 4, 1, 1, 0.0d, 1.0d,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, defInsets,
				0, 0);

		SpeedPanel speedPanel = new SpeedPanel(layouter);

		gbl.setConstraints(speedPanel, gbc);
		panel.add(speedPanel, gbc);
		dc.registerModelPropertyListener(speedPanel);

		gbc = new GridBagConstraints(1, 4, 1, 1, 0.0d, 0.1d,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, defInsets,
				0, 0);

		PitchPanel pitchPanel = new PitchPanel(layouter);

		gbl.setConstraints(pitchPanel, gbc);
		panel.add(pitchPanel, gbc);
		dc.registerModelPropertyListener(pitchPanel);

		gbc = new GridBagConstraints(0, 5, 2, 1, 0.0d, 1.0d,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, defInsets,
				0, 0);

		NotesPanel notesPanel = new NotesPanel(layouter);
		gbl.setConstraints(notesPanel, gbc);
		panel.add(notesPanel, gbc);
		dc.registerControllerPropertyChangeListener(notesPanel);

	}

	private void createMapPanel(JPanel panel) {

		GridBagLayout gbl = new GridBagLayout();
		panel.setLayout(gbl);

		GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1.0d, 1.0d,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, defInsets,
				0, 0);
		MapPanel mapPanel = new MapPanel(dc.getWPs(), dc.getMapImage());
		panel.add(mapPanel, gbc);
		dc.registerModelPropertyListener(mapPanel);

		layouter.layout(mapPanel);
	}

	private void createTopPanel(JPanel panel) {
		GridBagLayout gbl = new GridBagLayout();
		panel.setLayout(gbl);

		GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 0.25d,
				1.0d, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				defInsets, 2, 0);

		DepthPanel depthPanel = new DepthPanel(layouter);

		gbl.setConstraints(depthPanel, gbc);
		panel.add(depthPanel, gbc);
		dc.registerModelPropertyListener(depthPanel);

		gbc = new GridBagConstraints(1, 0, 1, 1, 0.5d, 1.0d,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, defInsets,
				15, 0);

		CompassPanel compassPanel = new CompassPanel(layouter);

		gbl.setConstraints(compassPanel, gbc);
		panel.add(compassPanel, gbc);
		dc.registerModelPropertyListener(compassPanel);

		gbc = new GridBagConstraints(2, 0, 1, 1, 0.25d, 1.0d,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, defInsets,
				15, 0);

		DiveTimePanel diveTimePanel = new DiveTimePanel(layouter);

		gbl.setConstraints(diveTimePanel, gbc);
		panel.add(diveTimePanel, gbc);
		dc.registerModelPropertyListener(diveTimePanel);

	}

}
