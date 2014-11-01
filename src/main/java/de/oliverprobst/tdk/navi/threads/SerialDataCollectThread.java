package de.oliverprobst.tdk.navi.threads;

import java.util.AbstractQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialFactory;

import de.oliverprobst.tdk.navi.serial.SerialPackage;

/**
 * Check Serial port for incoming byte stream and parse it according to the
 * protocol. Put it in the incoming queue then to be processed by the
 * {@link DataProcessingThread}.
 */
public class SerialDataCollectThread extends AbstractCollectThread {

	public static final int DELAY = 50;

	private static Logger log = LoggerFactory
			.getLogger(SerialDataCollectThread.class);

	private boolean end = false;

	private final AbstractQueue<SerialPackage> incoming;

	private int iteration = 0;

	final Serial serial = SerialFactory.createInstance();

	public SerialDataCollectThread(AbstractQueue<SerialPackage> incoming) {
		this.incoming = incoming;
	}

	public void end() {
		end = true;
	}

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
		log.info("Starting Data Collector for Serial connection");

		serial.open(Serial.DEFAULT_COM_PORT, 115200);

		StringBuilder sb = new StringBuilder();
		// int iteration = 0;

		while (!end) {
			// collect some debug information
			if (log.isDebugEnabled()) {
				logState();
			}
			
			if (serial.isOpen() && serial.availableBytes() > 3) {
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
					try {
						incoming.add(received);
						registerProcessedEvent();
					} catch (IllegalStateException e) {
						log.warn("Incoming Queue full. Discarding "
								+ incoming.size() + " messages.");
						incoming.clear();
					}
				} else {
					log.warn("Discarded invalid Serial Event: '" + message
							+ "'. Checksum is "
							+ received.getReceivedChecksum() + "; expected "
							+ received.getCalculatedCheckSum());
				}

				iteration++;
				if (iteration == 50) {
					log.trace("Send 0.");
					serial.write((byte) 0x6F);
				} else if (iteration == 100) {
					serial.write((byte) 0x70);
					log.trace("Send 1.");
					iteration = 0;
				} else {
					serial.write((byte) 0x00);
				}

			} else {
				if (serial.isClosed()){
					log.info("Serial port closed. Try to reopen!");
					serial.open(Serial.DEFAULT_COM_PORT, 115200);
				}
				try {
					Thread.sleep(DELAY);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		log.info("Ended Data Collector for Serial Bus");

	}
}
