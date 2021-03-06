package de.oliverprobst.tdk.navi;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.AbstractQueue;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oliverprobst.tdk.navi.config.Configuration;
import de.oliverprobst.tdk.navi.config.NaviMap;
import de.oliverprobst.tdk.navi.config.loader.ConfigurationFactory;
import de.oliverprobst.tdk.navi.config.loader.ConfigurationFailureException;
import de.oliverprobst.tdk.navi.config.loader.ConfigurationLoader;
import de.oliverprobst.tdk.navi.controller.DefaultController;
import de.oliverprobst.tdk.navi.dto.PitchAndCourse;
import de.oliverprobst.tdk.navi.gui.DemoDialog;
import de.oliverprobst.tdk.navi.gui.DemoKeyListener;
import de.oliverprobst.tdk.navi.gui.MainDialog;
import de.oliverprobst.tdk.navi.serial.SerialPackage;
import de.oliverprobst.tdk.navi.threads.AbstractCollectThread;
import de.oliverprobst.tdk.navi.threads.DataProcessingThread;
import de.oliverprobst.tdk.navi.threads.DemoDataCollectThread;
import de.oliverprobst.tdk.navi.threads.EntertainmentThread;
import de.oliverprobst.tdk.navi.threads.LogDiveDataThread;
import de.oliverprobst.tdk.navi.threads.SerialDataCollectThread;

/**
 * 
 * TODO // Once you have your heading, you must then add your 'Declination
 * Angle', which is the 'Error' of the magnetic field in your location. // Find
 * yours here: http://www.magnetic-declination.com/ // Mine is: -13* 2' W, which
 * is ~13 Degrees, or (which we need) 0.22 radians // If you cannot find your
 * Declination, comment out these two lines, your compass will be slightly off.
 */
public class App {
	private static Logger log = LoggerFactory.getLogger(App.class);

	private final static AbstractQueue<SerialPackage> incoming = new ArrayBlockingQueue<SerialPackage>(
			DataProcessingThread.MAX_BUFFER_SIZE);
	private static MainDialog md = null;

	private static Configuration config;

	private static AbstractCollectThread dataProcessingThread = null;
	private static SerialDataCollectThread collectorThread = null;
	private static LogDiveDataThread lddThread = null;
	private static EntertainmentThread entertainmentThread = null;

	/**
	 * @return the config
	 */
	public static Configuration getConfig() {
		return config;
	}

	public static void main(String[] args) {
		log.info("Starting Dive Software of Tief-Dunkel-Kalt.org");

		Locale.setDefault(Locale.US); // TODO : Misusage of Decimal converter
										// forces that. FIXIT!

		config = loadConfiguration();

		int loginterval = 1000;
		if (config.getSettings().getLogInterval() != null) {
			loginterval = config.getSettings().getLogInterval().intValue();
		}
		DefaultController dc = new DefaultController(loginterval);

		LocationEstimator.getInstance().init(config);

		boolean demo = config.getSettings().isDemomode();

		NaviMap useMap = (NaviMap) config.getSettings().getForcemap();
		if (useMap == null) {
			useMap = determineMapByGPS();
		}

		configureApp(dc, config, useMap);

		md = new MainDialog(dc);

		// notes must be set after MainDialog instanciation
		dc.setNotes(config.getSettings().getNotes());

		if (demo) {
			runInDemoMode(dc, config);
		} else {

			try {
				startDataCollect(dc, config);
			} catch (Throwable e) {
				log.error(
						"Could not start data collection thread. Demomode disabled by configuration. Exiting. Reason: "
								+ e.getMessage(), e);
				System.exit(-1);
			}
		}

	}

	private static NaviMap determineMapByGPS() {
		log.error("Not implemented yet.");
		return null;
	}

	/**
	 * @return Configuration read from home/user/TDKNaviConfig.xml
	 */
	private static Configuration loadConfiguration() {
		String fileName = "${user.home}" + File.separator + "TDKNaviConfig.xml";
		Configuration config = null;
		ConfigurationLoader cl = ConfigurationFactory.getConfigurationLoader();
		try {
			config = cl.loadConfig(fileName);
		} catch (IOException | ConfigurationFailureException e) {
			log.error("Failed to load configuration file " + fileName, e);
		}
		if (config == null) {
			log.info("Found no configuration file. Using internal default configuration.");
			try {
				InputStream is = App.class
						.getResourceAsStream("/TDKNaviConfig.xml");
				config = cl.loadConfig(is);
			} catch (IOException | ConfigurationFailureException e) {
				throw new RuntimeException(
						"Could not load internal configuration file.", e);
			}
		}
		return config;
	}

	/**
	 * Shutdown of the system initiated
	 */
	public static void shutdown() {
		if (collectorThread != null) {
			collectorThread.end();
		}
		if (dataProcessingThread != null) {
			dataProcessingThread.end();
		}
		if (lddThread != null) {
			lddThread.end();
		}
	}

	private static void startDataCollect(final DefaultController dc,
			final Configuration config) throws Exception {

		collectorThread = new SerialDataCollectThread(incoming);
		dataProcessingThread = new DataProcessingThread(incoming, dc);
		lddThread = new LogDiveDataThread(dc);
		entertainmentThread = new EntertainmentThread(dc);
		lddThread.setPriority(3);

		dataProcessingThread.setUncaughtExceptionHandler(uch);
		collectorThread.setUncaughtExceptionHandler(uch);
		lddThread.setUncaughtExceptionHandler(uch);

		collectorThread.start();
		dataProcessingThread.start();
		lddThread.start();
		entertainmentThread.start();
	}

	private static void runInDemoMode(DefaultController dc, Configuration config) {

		md.setLocation(100, 100);

		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice();
		int height = gd.getDisplayMode().getHeight();

		if (height > 900) {
			log.info("Screen is big enough to display help screen.");
			DemoDialog dlg = new DemoDialog();

			dlg.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			dlg.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent we) {
					log.info("System exit by closing documentation window. Bye!");
					System.exit(0);
				}
			});

			md.requestFocus();
		}

		DemoDataCollectThread collectorThread = new DemoDataCollectThread(dc,
				incoming);

		dataProcessingThread = new DataProcessingThread(incoming, dc);
		lddThread = new LogDiveDataThread(dc);
		lddThread.setPriority(3);

		dataProcessingThread.setUncaughtExceptionHandler(uch);
		collectorThread.setUncaughtExceptionHandler(uch);
		lddThread.setUncaughtExceptionHandler(uch);

		dataProcessingThread.start();
		collectorThread.start();
		lddThread.start();

		md.addKeyListener(new DemoKeyListener(dc, collectorThread));

	}

	private static UncaughtExceptionHandler uch = new UncaughtExceptionHandler() {
		public void uncaughtException(Thread t, Throwable e) {

			if (t instanceof DataProcessingThread) {
				DataProcessingThread thread = (DataProcessingThread) t;

				log.error("Data processing thread " + t.getName()
						+ " died. Trying to restart.", e);
				dataProcessingThread = new DataProcessingThread(incoming,
						thread.getDefaultController());
				dataProcessingThread.setUncaughtExceptionHandler(uch);
				dataProcessingThread.start();

			} else if (t instanceof DemoDataCollectThread) {
				log.error("Demo processing thread " + t.getName()
						+ " died. Please fix me!", e);
				System.exit(-2);

			} else if (t instanceof SerialDataCollectThread) {
				log.error("Serial data collect thread " + t.getName()
						+ " died. Trying to restart.", e);
				SerialDataCollectThread collectorThread = new SerialDataCollectThread(
						incoming);
				collectorThread.setUncaughtExceptionHandler(uch);
				collectorThread.start();

			} else if (t instanceof LogDiveDataThread) {
				log.error("Log Dive Data Thread " + t.getName()
						+ " died. No log data will be stored any more.", e);

			}
		}
	};

	/**
	 * @param dc
	 * @param config
	 * @param demoMap
	 */
	private static void configureApp(DefaultController dc,
			Configuration config, NaviMap map) {
		GeoCalculator hc = GeoCalculator.getInstance();

		hc.setNwCorner(map.getNorthwest().getLatitude(), map.getNorthwest()
				.getLongitude());
		hc.setSeCorner(map.getSoutheast().getLatitude(), map.getSoutheast()
				.getLongitude());
		hc.calculateDimension();

		for (de.oliverprobst.tdk.navi.config.Waypoint wp : map.getWaypoint()) {
			dc.getWPs().add(wp);
		}

		dc.setMapImage(map.getImage());
		dc.setBrightTheme(map.isBrightTheme());

		PitchAndCourse.setMagneticDeclination(map.getDeclination());

	}

}
