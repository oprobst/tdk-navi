package de.oliverprobst.tdk.navi.controller;

import de.oliverprobst.tdk.navi.dto.StructuralIntegrity;

public class StructuralIntegrityController {

	public StructuralIntegrityController() {

	}

	private boolean sternWasBroken = false;

	private boolean bowWasBroken = false;

	private boolean ambientWasBroken = false;

	public StructuralIntegrity parseCode(int hullIntegrityCode, float depth) {
		StructuralIntegrity si = new StructuralIntegrity();
		si.setLastCode(hullIntegrityCode);

		if (hullIntegrityCode >= 100000) {
			si.setStern(StructuralIntegrity.Status.BROKEN);
			hullIntegrityCode -= 100000;
			sternWasBroken = true;
		} else if (sternWasBroken) {
			si.setStern(StructuralIntegrity.Status.PROBLEMATIC);
		} else {
			si.setStern(StructuralIntegrity.Status.OK);
		}

		if (hullIntegrityCode >= 10000) {
			si.setBow(StructuralIntegrity.Status.BROKEN);
			hullIntegrityCode -= 10000;
			bowWasBroken = true;

		} else if (bowWasBroken) {
			si.setBow(StructuralIntegrity.Status.PROBLEMATIC);
		} else {
			si.setBow(StructuralIntegrity.Status.OK);

		}

		si.setPressure(hullIntegrityCode);
		calcAmbientPressureLeakDetection(si, depth);
		return si;
	}

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
