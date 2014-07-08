package de.oliverprobst.tdk.navi.threads;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oliverprobst.tdk.navi.controller.DefaultController;

public class DemoDataCollectThread extends Thread {

	private static Logger log = LoggerFactory
			.getLogger(DemoDataCollectThread.class);

	private final DefaultController dc;

	private int iteration = 0;

	public DemoDataCollectThread(DefaultController dc) {
		this.dc = dc;
		dc.setTemperature(24.2f);
		dc.setDepth(0.0f);
		dc.setCourse(180);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		log.info("Starting Data Collector in Demo Mode");
		while (true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				log.error("Thread sleep interrupted!", e);

			}

			writeDepth();
			writeCourse();
			writeTemp();
			writeHumidity();
			writeVoltage();

			writeGPS();

			if (iteration < 10000) {
				iteration++;
			} else {
				iteration = 0;
			}
		}
	}

	private void writeVoltage() {
		if (iteration < 100) {
			dc.setVoltage(4.18f);
		} else if (iteration < 150) {
			dc.setVoltage(4.12f);
		} else if (iteration < 200) {
			dc.setVoltage(4.07f);
		} else if (iteration < 200) {
			dc.setVoltage(4.01f);
		} else if (iteration < 250) {
			dc.setVoltage(3.92f);
		} else if (iteration % 40 == 0) {
			dc.setVoltage(((float) (Math.random() + 2.9) * 10) / 10);
		}

	}

	private void writeHumidity() {
		if (iteration % 50 == 0) {
			int c = (int) ((Math.random()) * 40) + 30;
			dc.setHumidity(c);
		}
	}

	double lastGPSLat = 4738.554;
	double lastGPSLong =  912.830;

	/**
	 * 
	 */
	private void writeGPS() {
		if (iteration % 30 == 0) {

			SimpleDateFormat dateFormatGmt = new SimpleDateFormat("HH:mm:ss.00");
			dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));

			// Corners:
			// SW 4738.541/00912.710
			// NW 4739.018/00912.710
			// SE 4738.541/00913.672

			// Distances:
			// NS: 4739.018 - 4738.541  
			// EW: 00913.672 - 00912.710

			lastGPSLat = lastGPSLat + (double) ((Math.random() -.2) * .006);
			lastGPSLong = lastGPSLong + (double) ((Math.random() -.35) * .0012);
			

			DecimalFormat formatterLng = new DecimalFormat("#0000.0000");
			DecimalFormat formatterLat = new DecimalFormat("#00000.0000");
			// Ka : 49° 0' 34" Nord, 8° 24' 15" Ost

			// $GPGGA,HHMMSS.ss,BBBB.BBBB,b,LLLLL.LLLL,l,Q,NN,D.D,H.H,h,G.G,g,A.A,RRRR*PP

			String message = "$GPGGA,161725.62," + formatterLng.format(lastGPSLat)
					+ ",N," + formatterLat.format(lastGPSLong)
					+ ",E,1,06,1.10,193.6,M,47.4,M,,*59";
			System.out.println(message);
			// (checksum invalid)
			dc.setGGA(message);
		}
	}

	private void writeCourse() {
		int course = dc.getCurrentRecordClone().getCourse();

		int c = (int) (((Math.random()) - .35) * 2.5) + course;
		if (c > 360) {
			c = c - 360;
		}
		dc.setCourse(c);
	}

	private void writeDepth() {

		float depth = dc.getCurrentRecordClone().getDepth();
		if (iteration > 0) {
			if (depth < 10) {
				float c = (float) ((Math.random()) / 14);
				depth = depth + c;
			} else if (depth >= 10 && depth < 50) {
				float c = (float) ((Math.random() - 0.2) / 18);
				depth = depth + c;
			} else if (depth > 50) {
				float c = (float) ((Math.random() - 0.8) / 25);
				depth = depth + c;
			}
		} else {
			depth = 0.3f;
		}
		dc.setDepth(depth);
	}

	private void writeTemp() {
		float depth = dc.getCurrentRecordClone().getDepth();
		if (iteration % 20 == 0) {
			if (depth < 10) {
				dc.setTemperature((float) ((Math.random() * 5) + 15));

			} else if (depth >= 5 && depth < 15) {
				dc.setTemperature((float) ((Math.random() * 5) + 10));
			} else if (depth > 15) {
				dc.setTemperature((float) ((Math.random() * 5) + 4));
			}
		}
	}

}
