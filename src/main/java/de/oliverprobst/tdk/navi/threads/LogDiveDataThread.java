package de.oliverprobst.tdk.navi.threads;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oliverprobst.tdk.navi.controller.DefaultController;
import de.oliverprobst.tdk.navi.dto.DiveData;

/**
 * Stores the current dive data to disk.
 * 
 * @author <b></b>www.tief-dunkel-kalt.org</b><br>
 *         Oliver Probst <a
 *         href="mailto:oliverprobst@gmx.de">oliverprobst@gmx.de</a>
 */
public class LogDiveDataThread extends Thread {
	
	/** The logger */
	private static Logger log = LoggerFactory
			.getLogger(LogDiveDataThread.class);

	/** Reference to the list of all dive data recorded by controller */
	private final List<DiveData> recordedData;
	
	/** Pointer to the last record stored. */
	int storedRecords = 0;

	/** The store intervall to write data to disk*/
	private final int storeInterval;

	/**
	 * Instantiates a new log dive data thread.
	 *
	 * @param defaultController the default controller holding the list of all records.
	 * @param storeIntervall the store intervall to write the data to disk in msec.
	 */
	public LogDiveDataThread(DefaultController defaultController,
			int storeIntervall) {
		this.storeInterval = storeIntervall;
		this.recordedData = defaultController.getRecord();

	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		super.run();
		log.info("Starting Log Dive Data Thread");
		while (true) {

			for (int i = storedRecords; i < recordedData.size(); i++) {
				DiveData dd = recordedData.get(i);
				store(dd);
				storedRecords++;
			}
			try {
				Thread.sleep(storeInterval);
			} catch (InterruptedException e) {
				log.error("Thread sleep interrupted!", e);				
			}
		}

	}

	/**
	 * Store the recorded dive data to disk.
	 *
	 * @param dd
	 *            the dive data to record.
	 */
	private void store(DiveData dd) {
		// System.out.println("Storing " + dd);
		// TODO!
		final Kml kml = new Kml();
		kml.createAndSetPlacemark()
		   .withName("London, UK").withOpen(Boolean.TRUE)
		   .createAndSetPoint().addToCoordinates(-0.126236, 51.500152);
		kml.marshal(new File("HelloKml.kml"));
	}
}
