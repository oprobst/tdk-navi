package de.oliverprobst.tdk.navi.threads;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oliverprobst.tdk.navi.App;
import de.oliverprobst.tdk.navi.config.Entertainment;
import de.oliverprobst.tdk.navi.config.Video;
import de.oliverprobst.tdk.navi.controller.DefaultController;
import de.oliverprobst.tdk.navi.controller.DiveDataProperties;

/**
 * When reaching the last deco stopp, this thread starts a process showing
 * entertainment media.
 * 
 * 
 * @author <b></b>www.tief-dunkel-kalt.org</b><br>
 *         Oliver Probst <a
 *         href="mailto:oliverprobst@gmx.de">oliverprobst@gmx.de</a>
 */
public class EntertainmentThread extends AbstractCollectThread implements
		PropertyChangeListener {

	/** The ui default controller. */
	private final DefaultController dc;

	private static Logger log = LoggerFactory
			.getLogger(EntertainmentThread.class);

	private final Entertainment config;

	// first time on the activation depth after bottom runtime
	private Boolean activationReceived = null;

	/**
	 * Instantiates a new serial data collect thread.
	 *
	 * @param incoming
	 *            the incoming event queue
	 */
	public EntertainmentThread(DefaultController dc) {
		this.dc = dc;
		dc.registerModelPropertyListener(this);
		config = App.getConfig().getSettings().getEntertainment();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.oliverprobst.tdk.navi.threads.AbstractCollectThread#getLog()
	 */
	@Override
	protected Logger getLog() {
		return log;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		// wait initially for 30 seconds until start entertainment thread.
		// we give other threads more cpu time.
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			log.warn("Thread interrupted...", e);
		}

		log.info("Starting Entertainment Thread");

		long startupTimestamp = System.currentTimeMillis();
		long firstStart = this.config.getMinimumTimeSinceStartup().longValue()
				* 60 * 1000 + startupTimestamp;

		while (firstStart >= System.currentTimeMillis()) {
			// first startup time not reached. Check again in 10 seconds.
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				log.warn("Thread interrupted...", e);
			}
		}

		log.info("EntertainmentThread: Minimum dive time passed, entertainment ready to rumble.");

		if (activationReceived != null) {
			// reset former activation signal if exists (this signal was thrown
			// when going down)
			activationReceived = null;
		}

		// Ok, now we're ready to rumble!

		while (!super.isEnd()) {

			// check if we reached already activation limit.
			// if no depth signal appeared until now, we assume that the
			// depth measurement is deactivated (is null) and proceed anyway.
			while (activationReceived != null && !activationReceived) {
				// still not reached activation stopp in depth
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					log.warn("Thread interrupted...", e);
				}
			}

			log.info("EntertainmentThread: Activation depth reached. Starting entertainment.");
			dc.setEntertainmentRunning(true);
			
			// TODO Currently only the first video will be executed.
			Video playme = config.getVideo().get(0);
			Process p;
			try {
				p = new ProcessBuilder("/usr/bin/omxplayer", playme.getFile())
						.start();
			
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					log.warn("Thread interrupted...", e);
				}
				while (shallEntertainmentRun() && p.isAlive()) {
					// do nothing as long as there is no reason to interrupt the
					// video.
					// check that every 10 sec.
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						log.warn("Thread interrupted...", e);
					}
				}

				log.info("EntertainmentThread: Deactivation parameter received. Stopping entertainment.");

				p.destroy();
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					log.warn("Thread interrupted...", e);
				}
				if (p.isAlive()) {
					p.destroyForcibly();
				}
				activationReceived = false;
				dc.setEntertainmentRunning(false);
			} catch (IOException e1) {
				log.error("Could not start video player...", e1);
			}
		}

	}

	private boolean shallEntertainmentRun() {
		return true; // todo
	}

	private float depth = 0.0f;

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		if (evt.getPropertyName().equals(DiveDataProperties.PROP_DEPTH)) {
			depth = (Float) evt.getNewValue();
			if (config.getDepthActivate().floatValue() >= depth) {
				this.activationReceived = true;
			}
		}
	}
}
