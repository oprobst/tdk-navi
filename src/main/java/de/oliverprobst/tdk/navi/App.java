package de.oliverprobst.tdk.navi;

import java.lang.Thread.UncaughtExceptionHandler;
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

	private static DataProcessingThread dataProcessingThread = null;
	private static I2CDataCollectThread collectorThread = null;

	private static void startDataCollect(DefaultController dc) throws Exception {

		collectorThread = new I2CDataCollectThread(incoming);
		dataProcessingThread = new DataProcessingThread(incoming, dc);
		collectorThread.run();
		dataProcessingThread.run();

		UncaughtExceptionHandler uch = new UncaughtExceptionHandler() {
			public void uncaughtException(Thread t, Throwable e) {
				if (dataProcessingThread != null) {
					dataProcessingThread.end();
				}
				if (collectorThread != null) {
					collectorThread.end();
				}
				log.error("Thread "
						+ t.getName()
						+ " died. Exited the other Thread and ending data collection.");
				throw new RuntimeException(e);
			}
		};

		dataProcessingThread.setUncaughtExceptionHandler(uch);
		collectorThread.setUncaughtExceptionHandler(uch);

	}

	private static void runInDemoMode(DefaultController dc) {

		Thread collectorThread = new DemoDataCollectThread(dc);
		collectorThread.start();
	}
}
