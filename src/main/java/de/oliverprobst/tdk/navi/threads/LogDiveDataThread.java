package de.oliverprobst.tdk.navi.threads;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.LineString;
import de.oliverprobst.tdk.navi.controller.DefaultController;
import de.oliverprobst.tdk.navi.dto.DiveData;

/**
 * Stores the current dive data to disk.
 * 
 * @author <b></b>www.tief-dunkel-kalt.org</b><br>
 *         Oliver Probst <a
 *         href="mailto:oliverprobst@gmx.de">oliverprobst@gmx.de</a>
 */
public class LogDiveDataThread extends AbstractCollectThread {

	/** The logger */
	private static Logger log = LoggerFactory
			.getLogger(LogDiveDataThread.class);

	/** Reference to the list of all dive data recorded by controller */
	private final List<DiveData> recordedData;

	/** Pointer to the last record stored. */
	int storedRecords = 0;

	/** The store intervall to write data to disk */
	private final int storeInterval;

	/**
	 * Instantiates a new log dive data thread.
	 *
	 * @param defaultController
	 *            the default controller holding the list of all records.
	 * @param storeIntervall
	 *            the store intervall to write the data to disk in msec.
	 */
	public LogDiveDataThread(DefaultController defaultController) {
		this.storeInterval = 10000;
		this.recordedData = defaultController.getRecord();

		route = kml.createAndSetPlacemark().withName("Navi")
				.withOpen(Boolean.TRUE).createAndSetLineString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		super.run();
		log.info("Starting Log Dive Data Thread");
		while (!end) {

			for (int i = storedRecords; i < recordedData.size(); i++) {
				DiveData dd = recordedData.get(i);
				store(dd);
				storedRecords++;
			}
			try {
				Thread.sleep(storeInterval);
			} catch (InterruptedException e) {
				if (!end) {
					log.error("Thread sleep interrupted!", e);
				}
			}
		}

	}

	@Override
	public void end() {
		super.end();
		saveKmlToDisk();
		saveCsvToDisk();
	}

	private void saveCsvToDisk() {
		String date = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss-SSS")
				.format(new Date());
		String filename = "/home/pi/navi-" + date + ".csv"; //TODO make configurable
		Path target = Paths.get(filename);
		try {
			Path file = Files.createFile(target);
			StringBuffer filecontent = new StringBuffer();
			for (DiveData dd : recordedData) {
				filecontent.append(dd.toCommaSeparatedString());
			}
			Files.write(file, filecontent.toString().getBytes(),
					StandardOpenOption.WRITE);
			log.info("Stored " + filename);
		} catch (IOException ex) {
			log.error("Failed to write log data at " + filename, ex);
		}

	}

	private void saveKmlToDisk() {
		String filename = "/home/pi/navi-" + new Date().toString() + ".kml";
		try {
			String date = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss-SSS")
					.format(new Date());
			kml.marshal(new File("target/navi-" + date + ".kml"));
			log.info("Stored " + filename);
		} catch (FileNotFoundException e) {
			log.error("Could not write dive log file.", e);
		}
	}

	/** This variables triggers the thread to end. */
	private boolean end = false;

	final Kml kml = new Kml();
	private LineString route;

	/**
	 * Convert currend records to kml.
	 *
	 * @param dd
	 *            the dive data to record.
	 */
	private void store(DiveData dd) {
		if (dd != null && dd.getGga() != null && route != null) {
			route.addToCoordinates(dd.getGga().getLongitude(), dd.getGga()
					.getLatitude());
		}
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
}
