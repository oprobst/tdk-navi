package de.oliverprobst.tdk.navi;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oliverprobst.tdk.navi.config.Configuration;
import de.oliverprobst.tdk.navi.config.NaviMap;
import de.oliverprobst.tdk.navi.config.loader.ConfigurationFactory;
import de.oliverprobst.tdk.navi.config.loader.ConfigurationFailureException;
import de.oliverprobst.tdk.navi.config.loader.ConfigurationLoader;
import de.oliverprobst.tdk.navi.controller.DefaultController;
import de.oliverprobst.tdk.navi.gui.DemoDialog;
import de.oliverprobst.tdk.navi.gui.DemoKeyListener;
import de.oliverprobst.tdk.navi.gui.MainDialog;
import de.oliverprobst.tdk.navi.i2c.I2CPackage;
import de.oliverprobst.tdk.navi.threads.DataProcessingThread;
import de.oliverprobst.tdk.navi.threads.DemoDataCollectThread;
import de.oliverprobst.tdk.navi.threads.I2CDataCollectThread;

/**
 *  
 *
 */
public class App {
	private static Logger log = LoggerFactory.getLogger(App.class);

	private final static ConcurrentLinkedQueue<I2CPackage> incoming = new ConcurrentLinkedQueue<I2CPackage>();
	private static MainDialog md = null;

	public static void main(String[] args) {
		log.info("Starting Dive Software of Tief-Dunkel-Kalt.org");

		Locale.setDefault(Locale.US); // TODO : Misusage of Decimal converter
										// forces that. FIXIT!

		Configuration config = loadConfiguration();

		DefaultController dc = new DefaultController();

		boolean isDemoMode = config.getSettings().isDemomode();

		NaviMap useMap = (NaviMap) config.getSettings().getForcemap();
		if (useMap == null) {
			useMap = determineMapByGPS();
		}

		configureApp(dc, config, useMap);

		md = new MainDialog(dc);

		if (isDemoMode) {
			runInDemoMode(dc, config);
		} else {

			try {
				startDataCollect(dc, config);
			} catch (Throwable e) {
				log.error(
						"Could not start data collection thread. Going to demo mode. Reason: "
								+ e.getMessage(), e);
				runInDemoMode(dc, config);
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

	private static DataProcessingThread dataProcessingThread = null;
	private static I2CDataCollectThread collectorThread = null;

	private static void startDataCollect(final DefaultController dc,
			final Configuration config) throws Exception {

		collectorThread = new I2CDataCollectThread(incoming);
		dataProcessingThread = new DataProcessingThread(incoming, dc);

		UncaughtExceptionHandler uch = new UncaughtExceptionHandler() {
			public void uncaughtException(Thread t, Throwable e) {
				if (dataProcessingThread != null) {
					dataProcessingThread.end();
				}
				if (collectorThread != null) {
					collectorThread.end();
				}
				log.error(
						"Thread "
								+ t.getName()
								+ " died. Exited the other Thread and ending data collection.",
						e);

				log.info("Entering DEMO Mode due to previous exception.");
				runInDemoMode(dc, config);
			}
		};

		dataProcessingThread.setUncaughtExceptionHandler(uch);
		collectorThread.setUncaughtExceptionHandler(uch);

		collectorThread.start();
		dataProcessingThread.start();
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

		DemoDataCollectThread collectorThread = new DemoDataCollectThread(dc);
		collectorThread
				.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
					public void uncaughtException(Thread t, Throwable e) {
						log.error("Demo Thread " + t.getName()
								+ " died. Ending Application.", e);

						System.exit(-1);
					}
				});

		md.addKeyListener(new DemoKeyListener(dc, collectorThread));
		collectorThread.start();

	}

	/**
	 * @param dc
	 * @param config
	 * @param demoMap
	 */
	private static void configureApp(DefaultController dc,
			Configuration config, NaviMap map) {
		HaversineConverter hc = HaversineConverter.getInstance();

		hc.setNwCorner(map.getNorthwest().getLatitude(), map.getNorthwest()
				.getLongitude());
		hc.setSeCorner(map.getSoutheast().getLatitude(), map.getSoutheast()
				.getLongitude());
		hc.calculateDimension();

		for (de.oliverprobst.tdk.navi.config.Waypoint wp : map.getWaypoint()) {
			dc.getWPs().add(wp);
		}

		dc.setNotes(config.getSettings().getNotes());
		dc.setMapImage(map.getImage());
	}

}
