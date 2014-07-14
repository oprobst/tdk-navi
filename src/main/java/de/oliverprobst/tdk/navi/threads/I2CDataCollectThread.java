package de.oliverprobst.tdk.navi.threads;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

import de.oliverprobst.tdk.navi.i2c.I2CPackage;

public class I2CDataCollectThread extends Thread {

	private static Logger log = LoggerFactory
			.getLogger(I2CDataCollectThread.class);

	private final ConcurrentLinkedQueue<I2CPackage> incoming;

	public I2CDataCollectThread(ConcurrentLinkedQueue<I2CPackage> incoming) {
		this.incoming = incoming;
	}

	private I2CBus bus;
	private I2CDevice arduino;
	private final int maxLength = 90;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		log.info("Starting Data Collector for I2C Bus");

		try {
			bus = I2CFactory.getInstance(I2CBus.BUS_1);
			arduino = bus.getDevice(0x04);
		} catch (IOException e) {
			throw new RuntimeException("Could not connect to I2CBus.", e);
		}
		int iteration = 0;
		byte[] buffer = new byte[maxLength];

		while (!end) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				arduino.read(buffer, 0, maxLength);
			} catch (IOException e) {
				System.err.print("E");
				/*
				 * try { for (int i = 0; i< buffer.length ; i++){
				 * System.out.print((int) buffer [i]);
				 * 
				 * } System.out.println();
				 * log.error("Received IO Exception. Current buffer is '" + new
				 * String(buffer, "UTF-8") + "'. Exception message is: " +
				 * e.getMessage()); } catch (UnsupportedEncodingException e1) {
				 * throw new RuntimeException("Could not connect to I2CBus.",
				 * e1);
				 * 
				 * }
				 */
				// throw new RuntimeException("Could not connect to I2CBus.",
				// e);
				// buffer = new byte[maxLength];
			}
			int eom = 0;
			for (int i = 0; i < maxLength; i++) {
				if (buffer[i] == 0x2a) { // 0x2a
					eom = i + 2;
					break;
				}
			}
			if (eom != 0) {
				final byte[] msgArr = new byte[eom + 1];
				System.arraycopy(buffer, 0, msgArr, 0, eom + 1);
				try {
					I2CPackage received = new I2CPackage(msgArr);

					if (received.isValid()) {
						incoming.add(received);
					} else {
						try {
							log.warn("Discarded invalid I2C Package: '"
									+ new String(msgArr, "UTF-8")
									+ "'. Checksum is "
									+ received.getReceivedChecksum()
									+ "; expected "
									+ received.getCalculatedCheckSum());
						} catch (UnsupportedEncodingException e) {
							log.error(e.getMessage(), e);
						}
					}

				} catch (IllegalArgumentException e) {

				}
			} else {
				try {
					log.warn("Received message without EndOfMessage Byte '*': '"
							+ new String(buffer, "UTF-8"));

					for (int i = 0; i < buffer.length; i++) {

						System.out.print((int) buffer[i]);

					}
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			iteration++;
			try {
				if (iteration == 1) {
					log.trace("Send 0.");
					arduino.write((byte) 0x00);
				} else if (iteration > 2) {
					arduino.write((byte) 0x01);
					log.trace("Send 1.");
					iteration = 0;
				}
			} catch (IOException e) {
			 log.error("Invalid i2c message received.",e);
			}

		}

		log.info("Ended Data Collector for I2C Bus");
	}

	private boolean end = false;

	public void end() {
		end = true;
	}
}
