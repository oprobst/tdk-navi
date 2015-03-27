package de.oliverprobst.tdk.navi.threads;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractQueue;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oliverprobst.tdk.navi.GeoCalculator;
import de.oliverprobst.tdk.navi.NmeaParser;
import de.oliverprobst.tdk.navi.controller.DefaultController;
import de.oliverprobst.tdk.navi.dto.DiveData;
import de.oliverprobst.tdk.navi.dto.PitchAndCourse;
import de.oliverprobst.tdk.navi.serial.SerialPackage;

/**
 * The Class simulate some demo data to test functionality without having
 * sensors connected.
 * 
 * @author <b></b>www.tief-dunkel-kalt.org</b><br>
 *         Oliver Probst <a
 *         href="mailto:oliverprobst@gmx.de">oliverprobst@gmx.de</a>
 */
public class DemoDataCollectThread extends AbstractCollectThread {

	/** The logger */
	private static Logger log = LoggerFactory
			.getLogger(DemoDataCollectThread.class);

	/** The ui default controller. */
	private final DefaultController dc;

	/** Shall gps events generated? */
	private boolean gpsActive = true;

	/** The incoming event queue to fill. */
	private final AbstractQueue<SerialPackage> incoming;

	/** Count the iteration to trigger some events every n loop. */
	private int iteration = 0;

	/** The last gps latitude. */
	double lastGPSLat = 4738.554; // = 47.642586

	/** The last gps longitude. */
	double lastGPSLong = 912.825; // = 009.213739

	/** The simulated vibration of dpv */
	private int simulatedVibration = 130;

	/**
	 * Instantiates a new demo data collect thread.
	 *
	 * @param dc
	 *            the ui default controller
	 * @param incoming2
	 *            the incoming event queue
	 */
	public DemoDataCollectThread(DefaultController dc,
			AbstractQueue<SerialPackage> incoming2) {
		this.dc = dc;
		this.incoming = incoming2;
		dc.setTemperature(24.2f);
		dc.setDepth(0.0f);
		dc.setGear(0);
		dc.setVoltage(12.34f);
		dc.setIntegrityCode("1,0,0988");
		dc.setPitchAndCourse(new PitchAndCourse(010, -4, 0));
	}

	/**
	 * Adds demo data set to incoming queueu
	 *
	 * @param sp
	 *            the incoming data
	 */
	private void addToQueue(SerialPackage sp) {
		try {
			incoming.add(sp);
		} catch (IllegalStateException e) {
			log.info("Incomming queue full, discarding demo message");
		}
	}

	/**
	 * Generate checksum similar to the arduino.
	 *
	 * @param msg
	 *            the msg without a checksum, terminated by '*'.
	 * @return the same message, but with a checksum
	 */
	public String generateChecksum(String msg) {

		int terminatorIndex = msg.indexOf('*');

		int pos = 1;
		int checksum_A = 0;
		int checksum_B = 0;

		do {

			int nextByte = msg.getBytes()[pos] & 0xff;
			checksum_A = (checksum_A + nextByte) & 0xff;
			checksum_B = (checksum_B + checksum_A) & 0xff;

		} while (pos++ < terminatorIndex); // includes terminator

		String chksum = new String(new char[] { (char) (checksum_A),
				(char) (checksum_B) });
		return msg + chksum;
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

	/**
	 * Gets the simulated vibration of dpv.
	 *
	 * @return the simulated vibration of dpv
	 */
	public int getSimulatedVibration() {
		return simulatedVibration;
	}

	/**
	 * Checks if is shall gps events generated?.
	 *
	 * @return the shall gps events generated?
	 */
	public boolean isGpsActive() {
		return gpsActive;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		log.info("Starting Data Collector in Demo Mode");
		while (!isEnd()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				log.error("Thread sleep interrupted!", e);

			}

			writeDepth();
			writeCourse();
			writeTemp();
			writeHumidity();
			writeGPS();

			if (iteration < 10000) {
				iteration++;
			} else {
				iteration = 0;
			}
		}
	}

	/**
	 * Sets the course.
	 *
	 * @param course
	 *            the course
	 * @param frPitch
	 *            the front-rear pitch
	 * @param lrPitxh
	 *            the left-right pitxh
	 */
	public void setCourse(int course, int frPitch, int lrPitxh) {
		String msg = "$b"
				+ ((int) course - PitchAndCourse.getMagneticDeclination())
				+ "," + frPitch + "," + lrPitxh + "*";
		msg = generateChecksum(msg);
		addToQueue(new SerialPackage(msg));
	}

	/**
	 * Sets the shall gps events generated?.
	 *
	 * @param gpsActive
	 *            the new shall gps events generated?
	 */
	public void setGpsActive(boolean gpsActive) {
		this.gpsActive = gpsActive;
	}

	/**
	 * Sets the leak message.
	 *
	 * @param string
	 *            the new leak message
	 */
	public void setLeakMessage(String string) {
		string = generateChecksum(string);
		addToQueue(new SerialPackage(string));
	}

	/**
	 * Sets the shutdown received.
	 *
	 * @param string
	 *            the new shutdown received
	 */
	public void setShutdownReceived(String string) {
		string = generateChecksum(string);
		addToQueue(new SerialPackage(string));
	}

	/**
	 * Sets the simulated vibration of dpv.
	 *
	 * @param simulatedVibration
	 *            the new simulated vibration of dpv
	 */
	public void setSimulatedVibration(int simulatedVibration) {
		this.simulatedVibration = simulatedVibration;
		String message = "$e" + simulatedVibration + "*";
		log.trace("Simulate event '" + message + "'.");
		message = generateChecksum(message);

		addToQueue(new SerialPackage(message));
	}

	/**
	 * Sets the voltage.
	 *
	 * @param voltage
	 *            the new voltage
	 */
	public void setVoltage(float voltage) {
		String message = "$g" + voltage + "*";
		message = generateChecksum(message);
		log.trace("Simulate event '" + message + "'.");
		addToQueue(new SerialPackage(message));
	}

	/**
	 * Write course.
	 */
	private void writeCourse() {

		DiveData record = dc.getCurrentRecord();
		int course = record.getPitchAndCourse().getCourse();

		int c = (int) (((Math.random()) - .5) * 3) + course;

		if (c > 360) {
			c = c - 360;
		} else if (c < 0) {
			c = 360 - c;
		}

		if (record.getGga() != null) {
			GeoCalculator hc = GeoCalculator.getInstance();

			double ymax = hc.getNwCornerLat();
			double xmin = hc.getNwCornerLng();
			double ymin = hc.getSeCornerLat();
			double xmax = hc.getSeCornerLng();
			NmeaParser p = record.getGga();
			double y = p.getLatitude();
			double x = p.getLongitude();

			if (x < xmin) {
				c = (int) (70 + Math.random() * 40);
			}
			if (y < ymin) {
				c = (int) (0 + Math.random() * 40);
			}
			if (x > xmax) {
				c = (int) (250 + Math.random() * 40);
			}
			if (y > ymax) {
				c = (int) (160 + Math.random() * 40);
			}
		}
		record.getPitchAndCourse();
		setCourse(c, record.getPitchAndCourse().getFrontRearPitch(), record
				.getPitchAndCourse().getLeftRightPitch());

	}

	/**
	 * Write depth.
	 */
	private void writeDepth() {

		float depth = dc.getCurrentRecordClone().getDepth();

		float c = (float) ((Math.random() / 10 ) );
		depth = depth + c;

		if (depth < 0) {
			depth = 0;
		}

		String message = "$i" + depth + "*";

		message = generateChecksum(message);
		log.trace("Simulate event '" + message + "'.");
		addToQueue(new SerialPackage(message));

	}

	/**
	 * GPGGA,,,,,,0,00,99.99,,,,,,*48 GPGGA,175918.00,,,,,0,03,6.25,,,,,,*57E
	 * 
	 */
	private void writeGPS() {

		if (iteration % 30 == 0 && isGpsActive()) {

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
			int course = dc.getCurrentRecordClone().getPitchAndCourse()
					.getCourse();
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
			double speed = dc.getCurrentRecord().getGear();
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

			String message = "$aGPGGA,161725.62,"
					+ formatterLng.format(lastGPSLat) + ",N,"
					+ formatterLat.format(lastGPSLong) + ",E," + isDGPS
					+ ",06,1.10,193.6,M,47.4,M,,*";
			log.trace("Simulate event '" + message + "'.");

			message = generateChecksum(message);

			addToQueue(new SerialPackage(message));
		}
	}

	/**
	 * Write humidity.
	 */
	private void writeHumidity() {
		if (iteration % 50 == 0) {
			int c = (int) ((Math.random()) * 40) + 30;
			dc.setHumidity(c);
		}
	}

	/**
	 * Write temp.
	 */
	private void writeTemp() {
		float depth = dc.getCurrentRecordClone().getDepth();
		if (iteration % 20 == 0) {
			String message = "";
			if (depth < 10) {
				message = "$d" + (float) ((Math.random() * 5) + 15) + "*";
			} else if (depth >= 5 && depth < 15) {
				message = "$d" + (float) ((Math.random() * 5) + 10) + "*";
			} else if (depth > 15) {
				message = "$d" + (float) ((Math.random() * 5) + 4) + "*";
			}

			message = generateChecksum(message);
			log.trace("Simulate event '" + message + "'.");
			addToQueue(new SerialPackage(message));
		}
	}

}
