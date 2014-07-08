package de.oliverprobst.tdk.navi.threads;

import java.io.IOException;
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
	private final int maxLength = 128;

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

			int iteration = 0;
			final byte[] buffer = new byte[maxLength];

			while (!end) {

				arduino.read(buffer, 0, maxLength);
				int eom = 0;
				for (int i = 0; i < maxLength; i++) {
					if (buffer[i] == 0x2a) {
						eom = i + 1;
						break;
					}
				}
				if (eom != 0) {
					final byte[] msgArr = new byte[eom + 1];
					System.arraycopy(buffer, 0, msgArr, 0, eom + 1);

					incoming.add(new I2CPackage(msgArr));

					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						log.info("Thread sleep interrupted", e);
					}
					iteration++;
					if (iteration == 100) {
						log.trace("Send 0.");
						arduino.write((byte) 0x00);
					} else if (iteration > 200) {
						arduino.write((byte) 0xFF);
						log.trace("Send FF.");
						iteration = 0;
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Could not connect to I2CBus.", e);
		}
		log.info("Ended Data Collector for I2C Bus");
	}

	private boolean end = false;

	public void end() {
		end = true;
	}
}
