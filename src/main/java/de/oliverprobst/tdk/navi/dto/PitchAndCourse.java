package de.oliverprobst.tdk.navi.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DTO Object for storing heading ant pitch
 * 
 * @author Oliver Probst
 */
public class PitchAndCourse implements Cloneable {

	private static double magneticDeclination = 2.0d;

	/**
	 * @return the magneticDeclination
	 */
	public static double getMagneticDeclination() {
		return magneticDeclination;
	}

	/**
	 * @param magneticDeclination
	 *            the magneticDeclination to set
	 */
	public static void setMagneticDeclination(double magneticDeclination) {
		PitchAndCourse.magneticDeclination = magneticDeclination;
	}

	private final int course;

	private final int frontRearPitch;

	private final int leftRightPitch;

	private static Logger log = LoggerFactory.getLogger(PitchAndCourse.class);

	/**
	 * Parse the incoming Construction String and create a new object. If String
	 * is invalid, null will be returned.
	 * 
	 * @param message
	 *            The received message from the sensors containing all required
	 *            data.
	 * @return new PitchAndCourse object, null if String is invalid
	 */
	public static PitchAndCourse construct(String message) {
		try {
			String[] split = message.split(",");
			int course = (int) Math.round(Double.parseDouble(split[0]));
			int frontRearPitch = (int) Math.round(Double.parseDouble(split[1]));
			int leftRightPitch = (int) Math.round(Double.parseDouble(split[2]));
			course += magneticDeclination;
			course = (course + 180) % 360;
			return new PitchAndCourse(course, frontRearPitch, leftRightPitch);
		} catch (Exception e) {
			log.warn(
					"Could not construct PitchAndCourse object due to invalid input message",
					e);
			return null;
		}

	}

	/**
	 * 
	 * ctor
	 * 
	 * @param course
	 *            current heading 0 - 360
	 * @param frontRearPitch
	 *            Pitch front rear
	 * @param leftRightPitch
	 *            Pith right left
	 */
	public PitchAndCourse(int course, int frontRearPitch, int leftRightPitch) {
		super();
		this.course = course;
		this.frontRearPitch = frontRearPitch;
		this.leftRightPitch = leftRightPitch;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	public PitchAndCourse clone() {
		return new PitchAndCourse(course, frontRearPitch, leftRightPitch);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof PitchAndCourse)) {
			return false;
		}
		PitchAndCourse other = (PitchAndCourse) obj;
		if (course != other.course) {
			return false;
		}
		if (frontRearPitch != other.frontRearPitch) {
			return false;
		}
		if (leftRightPitch != other.leftRightPitch) {
			return false;
		}
		return true;
	}

	/**
	 * @return the course
	 */
	public int getCourse() {
		return course;
	}

	/**
	 * @return the frontRearPitch
	 */
	public int getFrontRearPitch() {
		return frontRearPitch;
	}

	/**
	 * @return the leftRightPitch
	 */
	public int getLeftRightPitch() {
		return leftRightPitch;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + course;
		result = prime * result + frontRearPitch;
		result = prime * result + leftRightPitch;
		return result;
	}

}
