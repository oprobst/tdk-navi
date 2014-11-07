package de.oliverprobst.tdk.navi.threads;

import org.slf4j.Logger;

/**
 * Abstract implementation for all Threads of the navi.
 * 
 * Log the event count per second every configured intervall.
 * 
 * @author <b></b>www.tief-dunkel-kalt.org</b><br>
 *         Oliver Probst <a
 *         href="mailto:oliverprobst@gmx.de">oliverprobst@gmx.de</a>
 */
public abstract class AbstractCollectThread extends Thread {

	/**
	 * Every COLLECT_INTERVALL seconds, a log will be generated containing
	 * information about events per second.
	 */
	public final static int COLLECT_INTERVALL = 30;

	/** This variables triggers the thread to end. */
	private boolean end = false;

	/** The last log timestamp. */
	private long lastLogTimestamp = System.currentTimeMillis();

	/** Amount of messages processed since last log output */
	protected long processedCount = 0;

	/**
	 * Instantiates a new abstract collect thread.
	 */
	public AbstractCollectThread() {
		super();
	}

	/**
	 * Will end the thread.
	 */
	public void end() {
		end = true;
	}

	/**
	 * Must be implemented to give super class access to the logger.
	 *
	 * @return the log
	 */
	protected abstract Logger getLog();

	/**
	 * Checks if is this variables triggers the thread to end.
	 *
	 * @return the this variables triggers the thread to end
	 */
	protected boolean isEnd() {
		return end;
	}

	/**
	 * Log helper to log current process state if debug is enabled..
	 */
	protected void logState() {
		long now = System.currentTimeMillis();
		if (lastLogTimestamp < now - COLLECT_INTERVALL * 1000) {
			String msg = "Processed " + processedCount + " events in the last "
					+ (now - lastLogTimestamp) / 1000 + " seconds (="
					+ processedCount / COLLECT_INTERVALL + " events/sec).";
			getLog().debug(msg);
			lastLogTimestamp = now;
			processedCount = 0;
		}
	}

	/**
	 * Count the registered events for a thread
	 */
	public void registerProcessedEvent() {
		if (processedCount++ >= Integer.MAX_VALUE - 1) {
			processedCount = 0;
		}
	}
}