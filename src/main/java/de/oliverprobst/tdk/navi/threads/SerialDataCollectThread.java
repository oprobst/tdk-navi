package de.oliverprobst.tdk.navi.threads;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialFactory;

import de.oliverprobst.tdk.navi.serial.SerialPackage;

public class SerialDataCollectThread extends Thread {

	private static Logger log = LoggerFactory
			.getLogger(SerialDataCollectThread.class);

	private final ConcurrentLinkedQueue<SerialPackage> incoming;

	public SerialDataCollectThread(ConcurrentLinkedQueue<SerialPackage> incoming) {
		this.incoming = incoming;
	}

	public static final int DELAY = 10;

	final Serial serial = SerialFactory.createInstance();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		log.info("Starting Data Collector for Serial connection");

		serial.open(Serial.DEFAULT_COM_PORT, 38400);

		StringBuilder sb = new StringBuilder();
		//int iteration = 0;

		while (!end) {
			if (serial.isOpen() && serial.availableBytes() > 90) {
				// reset buffer data
				sb.setLength(0);

				char in = 0;
				boolean started = false;
				do {
					in = serial.read();
					if (in == '$') {
						started = true;
					}
					if (started) {
						sb.append(in);
					}
					if (in == '*') {
						sb.append(serial.read());
						sb.append(serial.read());
						break;
					}

				} while (in != '*');

				String message = sb.toString();
				log.trace("Received message: " + message);
				SerialPackage received = new SerialPackage(message);

				if (received.isValid()) {
					incoming.add(received);
				} else {
					log.warn("Discarded invalid Serial Event: '" + message
							+ "'. Checksum is "
							+ received.getReceivedChecksum() + "; expected "
							+ received.getCalculatedCheckSum());

				}

				// if (iteration == 1) {
				// log.trace("Send 0.");
				// serial.write((byte) 0x00);
				// serial.flush();
				// } else if (iteration > 1) {
				// serial.write((byte) 0x01);
				// serial.flush();
				// log.trace("Send 1.");
				// iteration = 0;
				// }

				// wait for a small interval before attempting to read
				// serial data again
				try {
					Thread.sleep(DELAY);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		log.info("Ended Data Collector for Serial Bus");

	}

	private boolean end = false;

	public void end() {
		end = true;
	}
}
