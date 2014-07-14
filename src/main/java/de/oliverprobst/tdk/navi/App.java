package de.oliverprobst.tdk.navi;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oliverprobst.tdk.navi.controller.DefaultController;
import de.oliverprobst.tdk.navi.dto.Waypoint;
import de.oliverprobst.tdk.navi.gui.DemoDialog;
import de.oliverprobst.tdk.navi.gui.MainDialog;
import de.oliverprobst.tdk.navi.i2c.I2CPackage;
import de.oliverprobst.tdk.navi.threads.DataProcessingThread;
import de.oliverprobst.tdk.navi.threads.DemoDataCollectThread;
import de.oliverprobst.tdk.navi.threads.I2CDataCollectThread;

/**
 * Hello world!
 *
 */
public class App {
	private static Logger log = LoggerFactory.getLogger(App.class);

	private final static ConcurrentLinkedQueue<I2CPackage> incoming = new ConcurrentLinkedQueue<I2CPackage>();
	private static MainDialog md = null;

	public static void main(String[] args) {
		log.info("Starting Dive Software of Tief-Dunkel-Kalt.org");

		Locale.setDefault(Locale.US); // TODO : Misusage of Decimal converter
										// forces that. FIXIT!
		DefaultController dc = new DefaultController();
		md = new MainDialog(dc);

		String demomode = System
				.getProperty("de.oliverprobst.tdk.navi.demomode");
		boolean isDemoMode = Boolean.getBoolean(demomode);

		if (isDemoMode) {
			runInDemoMode(dc);
		} else {
			try {
				startDataCollect(dc);
			} catch (Throwable e) {
				log.error(
						"Could not start data collection thread. Going to demo mode. Reason: "
								+ e.getMessage(), e);
				runInDemoMode(dc);
			}
		}
	}

	private static DataProcessingThread dataProcessingThread = null;
	private static I2CDataCollectThread collectorThread = null;

	private static void startDataCollect(final DefaultController dc)
			throws Exception {

		collectorThread = new I2CDataCollectThread(incoming);
		dataProcessingThread = new DataProcessingThread(incoming, dc);

		UncaughtExceptionHandler uch = new UncaughtExceptionHandler() {
			public void uncaughtException(Thread t, Throwable e) {
				if (dataProcessingThread != null) {
					dataProcessingThread.end();
				}
				if (collectorThread != null) {
					collectorThread.end();
				}
				log.error(
						"Thread "
								+ t.getName()
								+ " died. Exited the other Thread and ending data collection.",
						e);

				log.info("Entering DEMO Mode due to previous exception.");
				runInDemoMode(dc);
			}
		};

		dataProcessingThread.setUncaughtExceptionHandler(uch);
		collectorThread.setUncaughtExceptionHandler(uch);

		collectorThread.start();
		dataProcessingThread.start();
	}

	private static void runInDemoMode(DefaultController dc) {

		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice();
		int height = gd.getDisplayMode().getHeight();

		if (height > 480) {
			log.info("Screen is big enough to display help screen.");
			DemoDialog dlg = new DemoDialog();

			dlg.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			dlg.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent we) {
					log.info("System exit by closing documentation window. Bye!");
					System.exit(0);
				}
			});

			md.requestFocus();
		}

		HaversineConverter hc = HaversineConverter.getInstance();
		if (hc.getSeCornerLat() == 0) {
			// Demo map
			// SW 4738.541/00912.710
			// NW 4739.018/00912.710
			// SE 4738.541/00913.672

			//JURA
			//hc.setNwCorner(47.649500, 9.21120);
			//hc.setSeCorner(47.641400, 9.22675);
			//Gernsbach
			hc.setNwCorner(48.769124, 8.328957);
			hc.setSeCorner(48.765872, 8.335494);
			hc.calculateDimension();

			// Demo WPs JURA
			//dc.getWPs().add(new Waypoint("Jura", 47.647479, 9.224010));
			//dc.getWPs().add(new Waypoint("Entry", 47.642586, 9.213739));
			//dc.getWPs().add(new Waypoint("WP-1", 47.642816, 9.216398));
               
			dc.getWPs().add(new Waypoint("F.H.-Brücke", 48.769217, 8.334448));
			dc.getWPs().add(new Waypoint("Spielplatz",  48.766862, 8.333186));
			dc.getWPs().add(new Waypoint("Sitzbank",  48.769010, 8.330950));

			dc.setNotes(createDemoNodes());
		}

		Thread collectorThread = new DemoDataCollectThread(dc);
		collectorThread
				.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
					public void uncaughtException(Thread t, Throwable e) {
						log.error("Demo Thread " + t.getName()
								+ " died. Ending Application.", e);

						System.exit(-1);
					}
				});

		collectorThread.start();

	}

	private static String createDemoNodes() {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<p><b>Dive Notes</b></p>");
		sb.append("<table cellspacing=0 cellpadding=0>");
		sb.append("<tr><td>32m-25min &nbsp;</td> <td>36m-60min</td></tr>");
		sb.append("<tr><td>18m-1min</td><td>9m-9min</td></tr>");
		sb.append("<tr><td>15m-4min</td><td>6m-14min</td></tr>");
		sb.append("<tr><td>12m-5min</td><td>3m-29min</td></tr>");
		sb.append("</table></html>");
		return sb.toString();
	}
}
