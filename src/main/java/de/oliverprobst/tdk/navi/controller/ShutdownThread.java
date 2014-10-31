package de.oliverprobst.tdk.navi.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oliverprobst.tdk.navi.App;

/**
 * This thread will shut down computer or application only when in demomode.
 */
public class ShutdownThread extends Thread {

	private Logger log = LoggerFactory.getLogger(ShutdownThread.class);

	private int waitForUser = 10000;

	/**
	 * Instantiates a new shutdown thread.
	 *
	 * @param waitForUser
	 *            wait before shutdown this amount of millis
	 */
	public ShutdownThread(int waitForUser) {
		this.waitForUser = waitForUser;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		try {
			Thread.sleep(waitForUser);
		} catch (InterruptedException e) {
			log.error("Waiting for shutdown interrupted.", e);
		}
		try {
			if (App.getConfig().getSettings().isDemomode()) {
				log.info("Shutdown not executed in demo mode. Exiting application instead.");
				System.exit(0);
			} else {
				Runtime.getRuntime().exec("sudo shutdown -h now");
			}
		} catch (IOException e) {
			log.error("Failed to shutdown system!", e);
		}
	}

}
