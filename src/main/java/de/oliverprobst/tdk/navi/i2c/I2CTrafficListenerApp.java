package de.oliverprobst.tdk.navi.i2c;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oliverprobst.tdk.navi.threads.I2CDataCollectThread;

/**
 * This standalone application is for debugging purposes. It listens at the I2C
 * Interface and log all incoming messages.
 * 
 * 
 * @author <b></b>www.tief-dunkel-kalt.org</b><br>
 *         Oliver Probst <a
 *         href="mailto:oliverprobst@gmx.de">oliverprobst@gmx.de</a>
 */
public class I2CTrafficListenerApp {

	private static Logger log = LoggerFactory
			.getLogger(I2CTrafficListenerApp.class);

	private final static ConcurrentLinkedQueue<I2CPackage> incoming = new ConcurrentLinkedQueue<I2CPackage>();

	/**
	 * @param args
	 *            ignored...
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		log.info("Starting I2C Traffic Listener of Tief-Dunkel-Kalt.org");
		I2CDataCollectThread collectorThread = null;
		collectorThread = new I2CDataCollectThread(incoming);

		collectorThread
				.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

					public void uncaughtException(Thread t, Throwable e) {
						log.error("Thread " + t + " ended unexpectedly with a "
								+ e.getCause().getClass(), e);
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e1) {
							log.error("Thread sleep interrupted!", e1);
						}
						t.start();
					}
				});

		collectorThread.start();

		while (true) {
			log.info("Wait to process " +incoming.size() +" messages.");
			if (incoming.isEmpty()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					log.error("Thread sleep interrupted!", e);
				}
			} else {
				handle(incoming.remove());
			}

		}
	}
 

	private static void handle(I2CPackage msg) {
		log.info("--- New Message ------------------------- buffer size ="
				+ incoming.size());
		log.info("type    : " + msg.getType());
		log.info("payload : " + msg.getPayload());
		log.info("checksum: " + msg.getReceivedChecksum() + " == "
				+ msg.getCalculatedCheckSum() + " is " + msg.isValid());
	}

}