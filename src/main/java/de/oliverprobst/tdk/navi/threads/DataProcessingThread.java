package de.oliverprobst.tdk.navi.threads;

import java.util.AbstractQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oliverprobst.tdk.navi.NmeaParser;
import de.oliverprobst.tdk.navi.controller.DefaultController;
import de.oliverprobst.tdk.navi.dto.PitchAndCourse;
import de.oliverprobst.tdk.navi.serial.SerialPackage;

/**
 * This thread collects all incoming events from the queue, parse them and
 * convert them to {@link SerialPackage}.
 * 
 * Then they will be handed over to the main Controller.
 */
public class DataProcessingThread extends AbstractCollectThread {

	static Logger log = LoggerFactory.getLogger(DataProcessingThread.class);

	public final static int MAX_BUFFER_SIZE = 25;

	private final DefaultController dc;

	private boolean end = false;

	private final AbstractQueue<SerialPackage> incoming;

	public DataProcessingThread(AbstractQueue<SerialPackage> incoming,
			DefaultController dc) {
		this.incoming = incoming;
		this.dc = dc;
	}

	public void end() {
		end = true;
	}

	public DefaultController getDefaultController() {
		return dc;
	}

	@Override
	protected Logger getLog() {

		return log;
	}

	private void handle(SerialPackage message) {

		if (!message.isValid()) {
			// Usually filtered by SerialCollector Thread.
			log.info("Invalid message " + message);
			return;
		}

		String payload = message.getPayload();
		try {

			switch (message.getType()) {

			case NMEA_GGA:
				parseGga(payload);
				break;

			case COURSE:
				parseCourse(payload);
				break;

			case SPEED:
				parseSpeed(payload);
				break;

			case LEAK:
				parseLeak(payload);
				break;

			case DEPTH:
				parseDepth(payload);
				break;

			case TEMPERATURE:
				parseTemperature(payload);
				break;

			case HUMIDITY:
				parseHumidity(payload);

			case VOLTAGE:
				parseVoltage(payload);
				break;

			case SHUTDOWN:
				dc.shutdown(payload);
				break;

			default:
				log.warn("Message of type " + message.getType()
						+ " will be ignored: " + message);
				break;
			}

		} catch (Exception e) {
			log.error("Failure when parsing payload '" + payload
					+ "' of message '" + message + ". Discarded package. ", e);
		}
	}

	private void parseCourse(String payload) {
		PitchAndCourse pAc = PitchAndCourse.construct(payload);
		if (pAc != null) {
			dc.setPitchAndCourse(pAc);
		}
	}

	private void parseDepth(String payload) {
		float depth = Float.parseFloat(payload);
		depth = depth / 100;
		dc.setDepth(depth);
	}

	private void parseGga(String payload) {
		NmeaParser nmea = new NmeaParser(payload);
		if (nmea.isValid()) {
			dc.setGGA(nmea);
		}
	}

	private void parseHumidity(String payload) {
		int humidity = Integer.parseInt(payload);
		dc.setHumidity(humidity);
	}

	private void parseLeak(String payload) {
		dc.setIntegrityCode(payload);
	}

	private void parseSpeed(String payload) {
		try {
			dc.setGear(Integer.parseInt(payload));
		} catch (NumberFormatException e) {
			log.warn("Received a speed message containing no number.", e);
		}
	}

	private void parseTemperature(String payload) {
		float temperature = Float.parseFloat(payload);
		dc.setTemperature(temperature);
	}

	private void parseVoltage(String payload) {
		float voltage = Float.parseFloat(payload);
		dc.setVoltage(voltage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		log.info("Starting Data Processing Thread");

		while (!end) {
			if (!incoming.isEmpty()) {
				SerialPackage sp = incoming.remove();
				try {
					handle(sp);
					registerProcessedEvent();
				} catch (Exception e) {
					log.error("Exception occurred, discarded message " + sp, e);
				}
			}

			// collect some debug information
			if (log.isDebugEnabled()) {
				logState();
			}

		}
		log.info("Ended Data Processing Thread");
	}
}
