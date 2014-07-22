package de.oliverprobst.tdk.navi.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oliverprobst.tdk.navi.controller.DefaultController;
import de.oliverprobst.tdk.navi.dto.DiveData;
import de.oliverprobst.tdk.navi.dto.PitchAndCourse;
import de.oliverprobst.tdk.navi.dto.StructuralIntegrity.Status;
import de.oliverprobst.tdk.navi.threads.DemoDataCollectThread;

public class DemoKeyListener implements KeyListener {

	private static Logger log = LoggerFactory.getLogger(DemoKeyListener.class);
	private final DefaultController dc;
	private final DemoDataCollectThread collectorThread;

	public DemoKeyListener(DefaultController dc,
			DemoDataCollectThread collectorThread) {
		super();
		this.dc = dc;
		this.collectorThread = collectorThread;
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {

		DiveData dd = dc.getCurrentRecord();

		int frPitch = dd.getPitchAndCourse().getFrontRearPitch();
		int lrPitch = dd.getPitchAndCourse().getLeftRightPitch();

		int ssv = dd.getIntegrity().getSternSensorValue();
		int bsv = dd.getIntegrity().getBowSensorValue();
		int asv = dd.getIntegrity().getPressure();

		switch (e.getKeyCode()) {
		case 81: // q

			log.info("User input was 'Q', which means I've to leave... Good bye!");
			System.exit(0);
			break;
		case 87: // w
		case 38: // arr up
			float depth = dd.getDepth();
			depth -= .25;
			if (depth < 0) {
				depth = 0;
			}
			dc.setDepth(depth);
			break;
		case 83: // s
		case 40: // arr down
			depth = dd.getDepth();
			depth += .25;
			dc.setDepth(depth);
			break;

		case 69: // e
		case 39: // arr right
			int course = (int) (dd.getPitchAndCourse().getCourse() - PitchAndCourse
					.getMagneticDeclination());
			course += 2;
			if (course > 359) {
				course = 0;
			}
			collectorThread.setCourse(course, frPitch, lrPitch);
			break;
		case 68: // d
		case 37: // arr left
			course = (int) (dd.getPitchAndCourse().getCourse() - PitchAndCourse
					.getMagneticDeclination());
			course -= 2;
			if (course < 0) {
				course = 359;
			}
			collectorThread.setCourse(course, frPitch, lrPitch);
			break;
		case 32: // space
		case 82: // r
			collectorThread.setGpsActive(!collectorThread.isGpsActive());
			break;
		case 109: // -
			int vib = collectorThread.getSimulatedVibration();
			vib += 4;
			if (vib > 400) {
				vib = 400;
			}
			collectorThread.setSimulatedVibration(vib);
			break;
		case 107: // +
			vib = collectorThread.getSimulatedVibration();
			vib -= 4;
			if (vib < 0) {
				vib = 0;
			}
			collectorThread.setSimulatedVibration(vib);
			break;
		case 34:// pageDown
			frPitch -= 1;
			if (frPitch < -180) {
				frPitch = +180;
			}
			collectorThread.setCourse(dd.getPitchAndCourse().getCourse(),
					frPitch, lrPitch);
			break;
		case 33: // pageUp
			frPitch += 1;
			if (frPitch > 180) {
				frPitch = -180;
			}
			collectorThread.setCourse(dd.getPitchAndCourse().getCourse(),
					frPitch, lrPitch);
			break;
		case 36:// home
			lrPitch -= 1;
			if (lrPitch < -180) {
				lrPitch = +180;
			}
			collectorThread.setCourse(dd.getPitchAndCourse().getCourse(),
					frPitch, lrPitch);
			break;
		case 35: // end
			lrPitch += 1;
			if (lrPitch > 180) {
				lrPitch = -180;
			}
			collectorThread.setCourse(dd.getPitchAndCourse().getCourse(),
					frPitch, lrPitch);
			break;

		case 71: // g

			if (ssv > 0) {
				ssv = 0;
			} else {
				ssv = 100;
			}
			collectorThread.setLeakMessage("$c" + ssv + "," + bsv + "," + asv
					+ "*");

			break;
		case 84: // t
			if (bsv > 0) {
				bsv = 0;
			} else {
				bsv = 100;
			}
			collectorThread.setLeakMessage("$c" + ssv + "," + bsv + "," + asv
					+ "*");

			break;

		case 90: // z
			asv += 10;
			collectorThread.setLeakMessage("$c" + ssv + "," + bsv + "," + asv
					+ "*");
			break;
		case 72: // h
			asv -= 10;
			collectorThread.setLeakMessage("$c" + ssv + "," + bsv + "," + asv
					+ "*");
			break;
		case 70: // f
			dd.getIntegrity().setBow(Status.OK);
			dd.getIntegrity().setAmbient(Status.OK);
			dd.getIntegrity().setStern(Status.OK);
			break;

		default:
			// nada
			log.info("Unknown key: " + e.getKeyCode());
		}

	}

	public void keyReleased(KeyEvent e) {
	}

}
