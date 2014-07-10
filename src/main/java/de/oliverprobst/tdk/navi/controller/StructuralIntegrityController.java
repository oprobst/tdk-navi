package de.oliverprobst.tdk.navi.controller;

import de.oliverprobst.tdk.navi.dto.StructuralIntegrity;

public class StructuralIntegrityController {

	StructuralIntegrity si = new StructuralIntegrity();

	/**
	 * @return the si
	 */
	public StructuralIntegrity getSi() {
		return si;
	}

	public StructuralIntegrityController() {

	}

	public StructuralIntegrity parseCode(int hullIntegrityCode, float depth) {

		si.setLastCode(hullIntegrityCode);
		
		if (hullIntegrityCode >= 100000) {
			si.setStern(StructuralIntegrity.Status.BROKEN);
			hullIntegrityCode -= 100000;
		} else {
			if (si.getStern() == StructuralIntegrity.Status.BROKEN) {
				si.setStern(StructuralIntegrity.Status.PROBLEMATIC);
			} else {
				si.setStern(StructuralIntegrity.Status.OK);
			}
		}
		if (hullIntegrityCode >= 10000) {
			si.setBow(StructuralIntegrity.Status.BROKEN);
			hullIntegrityCode -= 10000;
		} else {
			if (si.getBow() == StructuralIntegrity.Status.BROKEN) {
				si.setBow(StructuralIntegrity.Status.PROBLEMATIC);
			} else {
				si.setBow(StructuralIntegrity.Status.OK);
			}
		}
		si.setPressure(hullIntegrityCode);
		calcAmbientPressureLeakDetection(depth);
		return si;
	}

	public StructuralIntegrity.Status calcAmbientPressureLeakDetection(
			float depth) {

		StructuralIntegrity.Status status = StructuralIntegrity.Status.OK;
		if (si.getPressure() > 1099) {
			status = StructuralIntegrity.Status.BROKEN;
		}

		si.setAmbient(status);
		return status;
	}

}
