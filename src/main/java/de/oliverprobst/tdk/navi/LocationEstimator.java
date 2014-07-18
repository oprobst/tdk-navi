package de.oliverprobst.tdk.navi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oliverprobst.tdk.navi.config.Configuration;
import de.oliverprobst.tdk.navi.config.Gear;

/**
 * Calculate distance and speed based on vibration measurement.
 * 
 * @author Oliver Probst
 */
public class LocationEstimator {

	/**
	 * singleton
	 */
	private static LocationEstimator instance = new LocationEstimator();

	private Logger log = LoggerFactory.getLogger(LocationEstimator.class);

	/**
	 * ctor
	 */
	private LocationEstimator() {

	}

	/**
	 * @return Highlander!
	 */
	public static LocationEstimator getInstance() {
		return instance;
	}

	List<GearSpeed> confGearSpeed = new ArrayList<>(10);

	/**
	 * Must be called first to store all speed values
	 * 
	 * @param config
	 *            The application configuration
	 */
	public void init(Configuration config) {
		confGearSpeed.add(new GearSpeed(0, 0));
		for (Gear speed : config.getSettings().getSpeed().getGear()) {
			confGearSpeed.add(new GearSpeed(speed.getSensor().intValue(), speed
					.getSpeed().intValue()));
		}
		Collections.sort(confGearSpeed);
	}

	public double calcScooterSpeed(double gear) {
		int round = (int) gear;

		GearSpeed lower = confGearSpeed.get(0);
		GearSpeed higher = confGearSpeed.get(0);

		int index = confGearSpeed.size() - round;
		if (confGearSpeed.size() - 1 >= index) {
			lower = confGearSpeed.get(index);
		}
		if (confGearSpeed.size() - 1 >= index - 1) {
			higher = confGearSpeed.get(index - 1);
		} else {
			higher = lower;
		}

		int hiSpeed = higher.speed;
		int lowSpeed = lower.speed;
		double remaining = (gear - round) * (hiSpeed - lowSpeed);

		double result = lowSpeed + remaining;
		if (log.isDebugEnabled()) {
			log.debug("Calculated speed for gear " + gear + ": " + result
					+ " which is " + lowSpeed  + " plus "+ remaining + " on the way between " + hiSpeed
					+ " and " + lowSpeed);
		}
		return result;

	}

	public double calcScooterGear(int vibration) {

		if (vibration < 30) {
			return 0;
		}
		GearSpeed determinedGearSpeed = confGearSpeed.get(0);
		GearSpeed determinedNextGearSpeed = confGearSpeed.get(0);
		int gear = confGearSpeed.size();
		for (GearSpeed gs : confGearSpeed) {
			if (gs.vibration > vibration) {
				determinedNextGearSpeed = gs;
				break;
			}
			gear--;
			determinedGearSpeed = gs;
		}

		double diff = determinedNextGearSpeed.vibration
				- determinedGearSpeed.vibration;
		double remainingVib = (vibration - determinedGearSpeed.vibration)
				/ diff;

		return gear + (1 - remainingVib);
	}

	class GearSpeed implements Comparable<GearSpeed> {
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "GearSpeed [vibration=" + vibration + ", speed=" + speed
					+ "]";
		}

		int vibration = 0;

		public GearSpeed(int vibration, int speed) {
			this.vibration = vibration;
			this.speed = speed;
		}

		int speed = 0;

		@Override
		public int compareTo(GearSpeed o) {
			return Integer.valueOf(vibration).compareTo(
					Integer.valueOf(o.vibration));
		}
	}

}
