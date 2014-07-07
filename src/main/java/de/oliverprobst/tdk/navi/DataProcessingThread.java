package de.oliverprobst.tdk.navi;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oliverprobst.tdk.navi.controller.DefaultController;
import de.oliverprobst.tdk.navi.i2c.I2CPackage;

public class DataProcessingThread extends Thread {

	private static Logger log = LoggerFactory
			.getLogger(DataProcessingThread.class);

	private final ConcurrentLinkedQueue<I2CPackage> incoming;

	private final DefaultController dc;

	public final static int MAX_BUFFER_SIZE = 100;
	public final static int MAX_BUFFER_DELETE_OFFSET = 10;

	public DataProcessingThread(ConcurrentLinkedQueue<I2CPackage> incoming,
			DefaultController dc) {
		this.incoming = incoming;
		this.dc = dc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		log.info("Starting Data Processing Thread");

		// try {

		while (!end) {
			if (incoming.isEmpty()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					log.error("Thread sleep interrupted!", e);
				}
			} else if (incoming.size() > MAX_BUFFER_SIZE) {
				int diff = MAX_BUFFER_SIZE - incoming.size()
						+ MAX_BUFFER_DELETE_OFFSET;
				for (int i = 0; i < diff; i++) {
					incoming.remove();
				}
				log.warn("Incoming event buffer full. Discarding " + diff
						+ " messages!");
			} else {
				handle(incoming.remove());
			}

		}
	}

	private void handle(I2CPackage message) {

		String payload = message.getPayload();
		// TODO
		switch (message.getType()) {

		case NMEA_GGA:
			parseGga(payload);
			break;

		case COURSE:

			break;

		case DEPTH:
			parseDepth(payload);
			break;

		case TEMPERATURE:
			parseTemperature(payload);
			break;

		case HUMIDITY:
			parseHumidity(payload);
		default:
			break;
		}

	}

	private void parseGga(String payload) {
		dc.setGGA(payload);
	}

	private void parseDepth(String payload) {
		float depth = Float.parseFloat(payload);
		depth = depth / 100;
		dc.setDepth(depth);
	}

	private void parseTemperature(String payload) {
		float temperature = Float.parseFloat(payload);
		temperature = temperature / 10;
		dc.setTemperature(temperature);
	}

	private void parseHumidity(String payload) {
		float humidity = Float.parseFloat(payload);
		dc.setHumidity(humidity);
	}

	private boolean end = false;

	public void end() {
		end = false;
	}
}
