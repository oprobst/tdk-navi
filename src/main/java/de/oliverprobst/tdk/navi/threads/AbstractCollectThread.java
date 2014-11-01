package de.oliverprobst.tdk.navi.threads;

import org.slf4j.Logger;

/**
 * Abstract implementation for all Threads of the navi.
 */
public abstract class AbstractCollectThread extends Thread {

	public final static int COLLECT_INTERVALL = 30;
	private long lastLogTimestamp = System.currentTimeMillis();
	protected long processedCount = 0;

	public AbstractCollectThread() {
		super();
	}

	public AbstractCollectThread(Runnable target) {
		super(target);
	}

	public AbstractCollectThread(Runnable target, String name) {
		super(target, name);
	}

	public AbstractCollectThread(String name) {
		super(name);
	}

	public AbstractCollectThread(ThreadGroup group, Runnable target) {
		super(group, target);
	}

	public AbstractCollectThread(ThreadGroup group, Runnable target, String name) {
		super(group, target, name);
	}

	public AbstractCollectThread(ThreadGroup group, Runnable target,
			String name, long stackSize) {
		super(group, target, name, stackSize);
	}

	public AbstractCollectThread(ThreadGroup group, String name) {
		super(group, name);
	}

	/**
	 * Must be implemented to give super class access to the logger.
	 *
	 * @return the log
	 */
	protected abstract Logger getLog();

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