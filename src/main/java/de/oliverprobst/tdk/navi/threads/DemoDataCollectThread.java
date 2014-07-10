package de.oliverprobst.tdk.navi.threads;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oliverprobst.tdk.navi.HaversineConverter;
import de.oliverprobst.tdk.navi.NmeaParser;
import de.oliverprobst.tdk.navi.controller.DefaultController;
import de.oliverprobst.tdk.navi.dto.DiveData;

public class DemoDataCollectThread extends Thread {

	private static Logger log = LoggerFactory
			.getLogger(DemoDataCollectThread.class);

	private final DefaultController dc;

	private int iteration = 0;

	public DemoDataCollectThread(DefaultController dc) {
		this.dc = dc;
		dc.setTemperature(24.2f);
		dc.setDepth(0.0f);
		dc.setCourse(045);
		dc.setSpeed(3);
		dc.setPitch(-8);
		dc.setIntegrityCode(998);
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
			writeIntegrity();

			if (iteration < 10000) {
				iteration++;
			} else {
				iteration = 0;
			}
		}
	}

	private void writeIntegrity() {
		if (iteration % 10 == 0) {
			int code = dc.getCurrentRecordClone().getIntegrity().getLastCode();
			code = ((int) (code / 10000)) * 10000;
			code = (int) (code + (dc.getCurrentRecordClone().getDepth() / 1000) * 1000) + 998;
			dc.setIntegrityCode(code);
		}
	}

	private void writeVoltage() {
		if (iteration < 100) {
			dc.setVoltage(12.18f);
		} else if (iteration < 150) {
			dc.setVoltage(12.1f);
		} else if (iteration < 200) {
			dc.setVoltage(11.97f);
		} else if (iteration < 200) {
			dc.setVoltage(11.74f);
		} else if (iteration < 250) {
			dc.setVoltage(11.60f);
		} else if (iteration % 40 == 0) {
			dc.setVoltage(((float) (Math.random() + 10.5) * 10) / 10);
		}

	}

	private void writeHumidity() {
		if (iteration % 50 == 0) {
			int c = (int) ((Math.random()) * 40) + 30;
			dc.setHumidity(c);
		}
	}

	
	double lastGPSLat = 4738.554; // = 47.642586 
	double lastGPSLong = 912.825; // = 009.213739

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
			double offY = 0;
			double offX = 0;
			int course = dc.getCurrentRecordClone().getCourse();
			if (course > 336 || course < 22) {
				offY = +1.0;
				offX = 0;
			} else if (course > 22 && course < 67) {
				offY = +.5;
				offX = +.5;
			} else if (course > 66 && course < 112) {
				offY = 0;
				offX = +1;
			} else if (course > 111 && course < 157) {
				offY = -.5;
				offX = +.5;
			} else if (course > 156 && course < 202) {
				offY = -1;
				offX = +0;
			} else if (course > 201 && course < 247) {
				offY = -.5;
				offX = -.5;
			} else if (course > 246 && course < 292) {
				offY = 0;
				offX = -1;
			} else if (course > 291 && course < 337) {
				offY = +.5;
				offX = -.5;
			}
			double speed = dc.getCurrentRecordClone().getSpeed();
			if (speed > 0) {
				speed = speed / 500 + .003;
			}
			lastGPSLong = lastGPSLong
					+ (double) (((Math.random() - .5) + offX) * speed);
			lastGPSLat = lastGPSLat
					+ (double) (((Math.random() - .5) + offY) * speed);

			DecimalFormat formatterLng = new DecimalFormat("#0000.00000");
			DecimalFormat formatterLat = new DecimalFormat("#00000.00000");
			// Ka : 49° 0' 34" Nord, 8° 24' 15" Ost

			// $GPGGA,HHMMSS.ss,BBBB.BBBB,b,LLLLL.LLLL,l,Q,NN,D.D,H.H,h,G.G,g,A.A,RRRR*PP

			int isDGPS = 0;
			isDGPS = (int) (Math.random() + 1.10);

			String message = "$GPGGA,161725.62,"
					+ formatterLng.format(lastGPSLat) + ",N,"
					+ formatterLat.format(lastGPSLong) + ",E," + isDGPS
					+ ",06,1.10,193.6,M,47.4,M,,*59";
			log.debug(message);
			// (checksum invalid)
			dc.setGGA(message);
		}
	}

	private void writeCourse() {
		DiveData record = dc.getCurrentRecordClone();
		int course = record.getCourse();

		int c = (int) (((Math.random()) - .5) * 0.8) + course;
		if (c > 360) {
			c = c - 360;
		}
		if (record.getGga() != null) {
			HaversineConverter hc = HaversineConverter.getInstance();

			double ymax = hc.getNwCornerLat();
			double xmin = hc.getNwCornerLng();
			double ymin = hc.getSeCornerLat();
			double xmax = hc.getSeCornerLng();
			NmeaParser p = new NmeaParser(record.getGga());
			double y = p.getLatitude();
			double x = p.getLongitude();  //TODO x/y not correct !?!

			if (x < xmin) {
				c = (int) (80 + Math.random() * 20);
			}
			if (y < ymin) {
				c = (int) (170 + Math.random() * 20);				
			}
			if (x > xmax) {
				c = (int) (260 + Math.random() * 20);
			}
			if (y > ymax) {
				c = (int)(0 + Math.random() * 10) ;
				
			}
		}

		dc.setCourse(c);
	}

	private void writeDepth() {

		float depth = dc.getCurrentRecordClone().getDepth();
		int pitch = dc.getCurrentRecordClone().getPitch() * -1;

		if (pitch > 90) {
			pitch = 180 - pitch;
		}
		if (pitch < -90) {
			pitch = -180 - pitch;
		}

		float c = (float) ((Math.random() * pitch) / 200);
		depth = depth + c;

		if (depth < 0) {
			depth = 0;
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
