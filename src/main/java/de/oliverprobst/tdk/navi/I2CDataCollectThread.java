package de.oliverprobst.tdk.navi;

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

			while (!end) {

				final byte[] buffer = new byte[maxLength];

				byte b = 0;
				int count = 0;
				while (b != 0x2a) {
					b = (byte) arduino.read();
					buffer[count] = b;
					if (count == 127) {
						// invalid message. Discard
						try {
							log.warn("Discarded invalid message :'"
									+ new String(buffer, "UTF-8") + "'.");
						} catch (UnsupportedEncodingException e) {
							throw new RuntimeException(e);
						}

						break;
					}
				}
				incoming.add(new I2CPackage(buffer));

			}
		} catch (IOException e) {
			throw new RuntimeException("Could not connect to I2CBus.", e);
		}
	}

	private boolean end = false;

	public void end() {
		end = false;
	}
}
