package de.oliverprobst.tdk.navi;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oliverprobst.tdk.navi.controller.DefaultController;
import de.oliverprobst.tdk.navi.gui.MainDialog;
import de.oliverprobst.tdk.navi.i2c.I2CPackage;
import de.oliverprobst.tdk.navi.threads.DataProcessingThread;
import de.oliverprobst.tdk.navi.threads.DemoDataCollectThread;
import de.oliverprobst.tdk.navi.threads.I2CDataCollectThread;

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
			} catch (Throwable e) {
				log.error(
						"Could not start data collection thread. Going to demo mode. Reason: "
								+ e.getMessage(), e);
				runInDemoMode(dc);
			}
		}
	}

	private static DataProcessingThread dataProcessingThread = null;
	private static I2CDataCollectThread collectorThread = null;

	private static void startDataCollect(final DefaultController dc)
			throws Exception {

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
				runInDemoMode(dc);
			}
		};

		dataProcessingThread.setUncaughtExceptionHandler(uch);
		collectorThread.setUncaughtExceptionHandler(uch);

		collectorThread.start();
		dataProcessingThread.start();
	}

	private static void runInDemoMode(DefaultController dc) {

		HaversineConverter hc = HaversineConverter.getInstance();
		if (hc.getSeCornerLat() == 0) {
			// Demo map
			// SW 4738.541/00912.710
			// NW 4739.018/00912.710
			// SE 4738.541/00913.672
			
			hc.setSeCorner(47.641875, 9.227410);
			hc.setNwCorner(47.651480, 9.211142);
			hc.calculateDimension();
		}

		Thread collectorThread = new DemoDataCollectThread(dc);
		collectorThread.start();
	}
}
