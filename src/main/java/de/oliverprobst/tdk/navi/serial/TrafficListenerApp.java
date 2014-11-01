package de.oliverprobst.tdk.navi.serial;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oliverprobst.tdk.navi.threads.SerialDataCollectThread;

/**
 * This standalone application is for debugging purposes. It listens at the
 * serial Interface and log all incoming messages.
 * 
 * 
 * @author <b></b>www.tief-dunkel-kalt.org</b><br>
 *         Oliver Probst <a
 *         href="mailto:oliverprobst@gmx.de">oliverprobst@gmx.de</a>
 */
public class TrafficListenerApp {

	private static Logger log = LoggerFactory
			.getLogger(TrafficListenerApp.class);

	private final static ConcurrentLinkedQueue<SerialPackage> incoming = new ConcurrentLinkedQueue<SerialPackage>();

	/**
	 * @param args
	 *            ignored...
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		log.info("Starting Serial Traffic Listener of Tief-Dunkel-Kalt.org");
		SerialDataCollectThread collectorThread = null;
		collectorThread = new SerialDataCollectThread(incoming);

		collectorThread
				.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

					public void uncaughtException(Thread t, Throwable e) {
						e.printStackTrace();
						log.error("Thread " + t + " ended unexpectedly with a "
								+ e.getCause().getClass(), e);
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e1) {
							log.error("Thread sleep interrupted!", e1);
						}
						e.printStackTrace();
						t.start();
					}
				});

		collectorThread.start();

		while (true) {
			//log.info("Wait to process " + incoming.size() + " messages.");
			if (incoming.isEmpty()) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					log.error("Thread sleep interrupted!", e);
				}
			} else {
				handle(incoming.remove());
			}
			if (incoming.size() > 100) {
				log.info("Clearing incomming queue with " + incoming + " messages.");
				incoming.clear();				
			}

		}
	}

	private static int courseCount = 0;

	private static void handle(SerialPackage msg) {
		if (msg.getType() == MessageType.COURSE && courseCount++ < 100) {
			return;
		} else if (msg.getType() == MessageType.COURSE) {
			courseCount = 0;
			log.info("--- New Message (1 of 100) --------------- buffer size ="
					+ incoming.size());
		} else {
			log.info("--- New Message ------------------------- buffer size ="
					+ incoming.size());
		}
		log.info("type    : " + msg.getType());
		log.info("payload : " + msg.getPayload());
		log.info("checksum: " + msg.getReceivedChecksum() + " == "
				+ msg.getCalculatedCheckSum() + " is " + msg.isValid());
	}

}