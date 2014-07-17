package de.oliverprobst.tdk.navi.threads;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataListener;
import com.pi4j.io.serial.SerialFactory;

import de.oliverprobst.tdk.navi.serial.SerialPackage;

public class SerialDataCollectThread extends Thread {

	private static Logger log = LoggerFactory
			.getLogger(SerialDataCollectThread.class);

	private final ConcurrentLinkedQueue<SerialPackage> incoming;

	public SerialDataCollectThread(ConcurrentLinkedQueue<SerialPackage> incoming) {
		this.incoming = incoming;
	}

	final Serial serial = SerialFactory.createInstance();

	private final ConcurrentLinkedQueue<Character> incomingChars = new ConcurrentLinkedQueue<Character>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		log.info("Starting Data Collector for Serial connection");

		serial.addListener(new SerialDataListener() {
			@Override
			public void dataReceived(SerialDataEvent event) {
				char cs[] = event.getData().toCharArray();
				for (char c : cs) {
					incomingChars.add(c);					
				}				
			}
		});

		serial.open(Serial.DEFAULT_COM_PORT, 38400);

		int iteration = 0;
		while (!end) {
			iteration++;

			while (incomingChars.size() < 100){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {					 
				}				
			}
			Character in = incomingChars.poll();
			
			boolean started = false;
			StringBuilder sb = new StringBuilder();
			while (!end) {
				 
				if (in == '$') {
					started = true;
					 
				}
				if (started) {
					sb.append(in);
					 
				}
				if (in == '*') {	
					sb.append(incomingChars.poll());
					sb.append(incomingChars.poll());
					break;
				}
				in = incomingChars.poll();
			}

			String message = sb.toString();
			log.trace("Received message: " + message);
			SerialPackage received = new SerialPackage(message);

			if (received.isValid()) {
				incoming.add(received);
			} else {
				log.warn("Discarded invalid Serial Event: '" + message
						+ "'. Checksum is " + received.getReceivedChecksum()
						+ "; expected " + received.getCalculatedCheckSum());

			}

			if (iteration == 1) {
				log.trace("Send 0.");
				serial.write((byte) 0x00);
			} else if (iteration > 1) {
				serial.write((byte) 0x01);
				log.trace("Send 1.");
				iteration = 0;
			}
		}

		log.info("Ended Data Collector for Serial Bus");
	}

	private boolean end = false;

	public void end() {
		end = true;
	}
}
