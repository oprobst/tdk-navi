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
 * 
 * @author <b></b>www.tief-dunkel-kalt.org</b><br>
 *         Oliver Probst <a
 *         href="mailto:oliverprobst@gmx.de">oliverprobst@gmx.de</a>
 */
public class SerialDataCollectThread extends AbstractCollectThread {

	/** The Constant COM_SPEED defines the serial connection speed. */
	public static final int COM_SPEED = 115200;

	/**
	 * Every LOG_WARN_DISCARDED discarded message will trigger a log output.
	 * Messages are discarded if the checksum is wrong.
	 */
	public static final short LOG_WARN_DISCARDED = 25;

	/** The log. */
	private static Logger log = LoggerFactory
			.getLogger(SerialDataCollectThread.class);

	/** Setting this flag to true will cause the thread to end. */
	private boolean end = false;

	/** The queue for all incomming events. */
	private final AbstractQueue<SerialPackage> incoming;

	/**
	 * Count the iterations (will be resetted when to high). Gives opportunity
	 * to trigger an action every n loop.
	 */
	private int iteration = 0;

	/** The serial connectivity to raspberry pi. */
	final Serial serial = SerialFactory.createInstance();

	/**
	 * Instantiates a new serial data collect thread.
	 *
	 * @param incoming
	 *            the incoming event queue
	 */
	public SerialDataCollectThread(AbstractQueue<SerialPackage> incoming) {
		this.incoming = incoming;
	}

	/**
	 * Cause the thread to end execution
	 */
	public void end() {
		end = true;
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
		log.info("Starting Data Collector for Serial connection");

		serial.open(Serial.DEFAULT_COM_PORT, COM_SPEED);

		StringBuilder sb = new StringBuilder();

		int discardedCount = 0;
		long lastDiscardedTimestamp = System.currentTimeMillis();
		while (true) {
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
						sb = new StringBuilder();

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
					log.trace("Discarded invalid Serial Event: '" + message
							+ "'. Checksum is "
							+ received.getReceivedChecksum() + "; expected "
							+ received.getCalculatedCheckSum());
					discardedCount++;
				}

				iteration++;
				if (iteration == 10) {
					log.trace("Send 0x6F.");
					serial.write((byte) 0x6F);
				} else if (iteration == 20) {
					serial.write((byte) 0x70);
					log.trace("Send 0x70.");
					iteration = 0;
				} else if (end) {
					serial.write("TERMINATE");
					break;
				} else {
					// serial.write((byte) 0x00);
				}
				if (discardedCount > LOG_WARN_DISCARDED) {
					long duration = (System.currentTimeMillis() - lastDiscardedTimestamp) / 1000;
					lastDiscardedTimestamp = System.currentTimeMillis();
					discardedCount = 0;
					log.warn("Discarded more than " + LOG_WARN_DISCARDED
							+ " invalid events in the last " + duration
							+ "seconds (=" + discardedCount / duration
							+ " events/sec).");
				}

			} else {
				if (serial.isClosed()) {
					log.info("Serial port closed. Try to reopen!");
					serial.open(Serial.DEFAULT_COM_PORT, 115200);
				}
			}
		}
		serial.close();
		log.info("Ended Data Collector for Serial Bus");

	}

}
