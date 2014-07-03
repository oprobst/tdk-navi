package de.oliverprobst.tdk.navi;

import java.text.SimpleDateFormat;
import java.util.Date;
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

			writeDepth(dc);
			writeCourse(dc);
			writeTemp(dc);

			if (iteration == 150 || iteration == 300) {

				SimpleDateFormat dateFormatGmt = new SimpleDateFormat(
						"HH:mm:ss.00");
				dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
				String time = dateFormatGmt.format(new Date());

				// Ka : 49° 0' 34" Nord, 8° 24' 15" Ost

				// $GPGGA,HHMMSS.ss,BBBB.BBBB,b,LLLLL.LLLL,l,Q,NN,D.D,H.H,h,G.G,g,A.A,RRRR*PP

				String message = "$GPGGA,161725.62,4846.13368,N,00819.92616,E,1,06,1.10,193.6,M,47.4,M,,*59";
				// (checksum invalid)
				dc.setGGA(message);
			}

			if (iteration < 10000) {
				iteration++;
			} else {
				iteration = 0;
			}
		}
	}

	private void writeCourse(DefaultController dc) {
		int course = dc.getCurrentRecordClone().getCourse();

		int c = (int) (((Math.random()) - .35) * 2.5) + course;
		if (c > 360) {
			c = c - 360;
		}
		dc.setCourse(c);
	}

	private void writeDepth(DefaultController dc) {

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

	private void writeTemp(DefaultController dc) {
		float depth = dc.getCurrentRecordClone().getDepth();
		if (iteration % 20 ==  0) {
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
