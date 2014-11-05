package de.oliverprobst.tdk.navi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oliverprobst.tdk.navi.config.Configuration;
import de.oliverprobst.tdk.navi.config.Gear;

/**
 * Experimental calculation of distance and speed based on vibration
 * measurement.
 * 
 * @author <b></b>www.tief-dunkel-kalt.org</b><br>
 *         Oliver Probst <a
 *         href="mailto:oliverprobst@gmx.de">oliverprobst@gmx.de</a>
 */
public class LocationEstimator {

	/**
	 * The Class GearSpeed is a simple DTO for relation between vibration and
	 * speed.
	 */
	class GearSpeed implements Comparable<GearSpeed> {
		int speed = 0;

		int vibration = 0;

		public GearSpeed(int vibration, int speed) {
			this.vibration = vibration;
			this.speed = speed;
		}

		@Override
		public int compareTo(GearSpeed o) {
			return Integer.valueOf(vibration).compareTo(
					Integer.valueOf(o.vibration));
		}

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
	}

	/**
	 * singleton
	 */
	private static LocationEstimator instance = new LocationEstimator();

	/**
	 * @return Highlander!
	 */
	public static LocationEstimator getInstance() {
		return instance;
	}

	/** The gear speed configuration as parsed from config file */
	List<GearSpeed> confGearSpeed = new ArrayList<>(10);

	/** The log. */
	private Logger log = LoggerFactory.getLogger(LocationEstimator.class);

	/**
	 * ctor
	 */
	private LocationEstimator() {

	}

	/**
	 * Try to calculate the dvp speed based on vibration measurement.
	 *
	 * @param vibration
	 *            the vibration measured by sensors
	 * @return the speed in meter per minute
	 */
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

	/**
	 * Try to calculate the dvp speed based on vibration measurement.
	 *
	 * @param gear
	 *            the gear estimation based on measured vibration.
	 * @return the speed in meter per minute
	 */
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
					+ " which is " + lowSpeed + " plus " + remaining
					+ " on the way between " + hiSpeed + " and " + lowSpeed);
		}
		return result;

	}

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

}
