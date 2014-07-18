package de.oliverprobst.tdk.navi.threads;

import java.util.AbstractQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oliverprobst.tdk.navi.controller.DefaultController;
import de.oliverprobst.tdk.navi.serial.SerialPackage;

public class DataProcessingThread extends Thread {

	private static Logger log = LoggerFactory
			.getLogger(DataProcessingThread.class);

	private final AbstractQueue<SerialPackage> incoming;

	private final DefaultController dc;

	public final static int MAX_BUFFER_SIZE = 20;

	public DataProcessingThread(AbstractQueue<SerialPackage> incoming,
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
			
			} else {
				handle(incoming.remove());
			}
			if (incoming.size() > MAX_BUFFER_SIZE) {
				log.warn("Incoming event buffer full. Discarding "
						+ incoming.size() + " messages!");
				incoming.clear();
			
			}
		}
		log.info("Ended Data Processing Thread");
	}

	private void handle(SerialPackage message) {

		String payload = message.getPayload();

		switch (message.getType()) {

		case NMEA_GGA:
			parseGga(payload);
			break;

		case COURSE:
			parseCourse(payload);
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

	private void parseCourse(String payload) {
		String[] split = payload.split(",");
		dc.setCourse((int) Math.round(Double.parseDouble(split[0])));
		dc.setPitch(((int) Math.round(Double.parseDouble(split[1]))) + ","
				+ ((int) Math.round(Double.parseDouble(split[2]))));// well... :-(
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
		int humidity = Integer.parseInt(payload);
		dc.setHumidity(humidity);
	}

	private boolean end = false;

	public void end() {
		end = true;
	}
}
