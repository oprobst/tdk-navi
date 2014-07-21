package de.oliverprobst.tdk.navi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oliverprobst.tdk.navi.dto.StructuralIntegrity;
import de.oliverprobst.tdk.navi.dto.StructuralIntegrity.Status;

public class StructuralIntegrityController {

	private Logger log = LoggerFactory
			.getLogger(StructuralIntegrityController.class);

	public StructuralIntegrityController() {

	}

	public StructuralIntegrity construct(String message, float depth) {
		try {
			String[] split = message.split(",");
			int sternSenor = (int) Math.round(Double.parseDouble(split[0].substring(2)));
			int bowSensor = (int) Math.round(Double.parseDouble(split[1]));
			int pressure = (int) Math.round(Double.parseDouble(split[2].substring(0,split[2].length()-1)));
			StructuralIntegrity si = new StructuralIntegrity();

			if (sternSenor > 50) {
				si.setStern(Status.BROKEN);
				sternWasBroken = true;
			} else if (sternWasBroken) {
				si.setStern(StructuralIntegrity.Status.PROBLEMATIC);
			} else {
				si.setStern(StructuralIntegrity.Status.OK);
			}

			if (bowSensor > 50) {
				si.setBow(Status.BROKEN);
				bowWasBroken = true;

			} else if (bowWasBroken) {
				si.setBow(StructuralIntegrity.Status.PROBLEMATIC);
			} else {
				si.setBow(StructuralIntegrity.Status.OK);

			}
			si.setBowSensorValue(bowSensor);
			si.setSternSensorValue(sternSenor);
			si.setPressure(pressure);
			StructuralIntegrity.Status status = StructuralIntegrity.Status.OK;
			if (pressure > 1099) {
				status = StructuralIntegrity.Status.BROKEN;
				ambientWasBroken = true;
			} else if (ambientWasBroken) {
				status = StructuralIntegrity.Status.PROBLEMATIC;
			} else {
				status = StructuralIntegrity.Status.OK;

			}

			si.setAmbient(status);
			calcAmbientPressureLeakDetection(si, depth);
			return si;
		} catch (Exception e) {
			log.warn(
					"Could not construct StructuralIntegrity object due to invalid input message",
					e);
			return null;
		}

	}

	private boolean sternWasBroken = false;

	private boolean bowWasBroken = false;

	private boolean ambientWasBroken = false;

	private StructuralIntegrity.Status calcAmbientPressureLeakDetection(
			StructuralIntegrity si, float depth) {

		StructuralIntegrity.Status status = StructuralIntegrity.Status.OK;
		if (si.getPressure() > 1099) {
			status = StructuralIntegrity.Status.BROKEN;
			ambientWasBroken = true;
		} else if (ambientWasBroken) {
			status = StructuralIntegrity.Status.PROBLEMATIC;
		} else {
			status = StructuralIntegrity.Status.OK;

		}

		si.setAmbient(status);
		return status;
	}

}
