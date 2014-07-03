package de.oliverprobst.tdk.navi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oliverprobst.tdk.navi.controller.DefaultController;
import de.oliverprobst.tdk.navi.gui.MainDialog;

/**
 * Hello world!
 *
 */
public class App {
	private static Logger log = LoggerFactory.getLogger(App.class);

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
			startDataCollect(dc);
		}
	}

	private static void startDataCollect(DefaultController dc) {
		log.error("Not implemented yet! Going to demo mode");
		runInDemoMode(dc);
	}

	private static void runInDemoMode(DefaultController dc) {
		
		Thread collectorThread = new DemoDataCollectThread(dc);
		collectorThread.start();
	}
}
