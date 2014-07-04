package de.oliverprobst.tdk.navi;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oliverprobst.tdk.navi.controller.DefaultController;
import de.oliverprobst.tdk.navi.gui.MainDialog;
import de.oliverprobst.tdk.navi.i2c.I2CPackage;

/**
 * Hello world!
 *
 */
public class App {
	private static Logger log = LoggerFactory.getLogger(App.class);

	private final static ConcurrentLinkedQueue<I2CPackage> incoming = new ConcurrentLinkedQueue<I2CPackage>();

	public static void main(String[] args) {
		log.info("Starting Dive Software of Tief-Dunkel-Kalt.org");
		DefaultController dc = new DefaultController();
		new MainDialog(dc);

		String demomode = System
				.getProperty("de.oliverprobst.tdk.navi.demomode");
		boolean isDemoMode = Boolean.getBoolean(demomode);

		if (isDemoMode) {
			runInDemoMode(dc);
		} else {
			try {
				startDataCollect(dc);
			} catch (Exception e) {
				log.error(
						"Could not start data collection thread. Going to demo mode. Reason: "
								+ e.getMessage(), e);
				runInDemoMode(dc);
			}
		}
	}

	private static void startDataCollect(DefaultController dc) throws Exception {
		DataProcessingThread dataProcessingThread = null;
		I2CDataCollectThread collectorThread = null;
		try {
			collectorThread = new I2CDataCollectThread(incoming);
			dataProcessingThread = new DataProcessingThread(incoming, dc);
		} catch (Exception e) {
			if (dataProcessingThread != null) {
				dataProcessingThread.end();
			}
			if (collectorThread != null) {
				collectorThread.end();
			}
			throw e;
		}

	}

	private static void runInDemoMode(DefaultController dc) {

		Thread collectorThread = new DemoDataCollectThread(dc);
		collectorThread.start();
	}
}
