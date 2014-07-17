package de.oliverprobst.tdk.navi.gui;

import java.awt.Point;

public class MapPoint extends Point {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6217537497089667062L;

	public MapPoint() {
		super(); 
	}

	public MapPoint(int x, int y) {
		super(x, y); 
	}

	public MapPoint(Point p) {
		super(p); 
	}

	private boolean estimated = false;

	/**
	 * @return the estimated
	 */
	public boolean isEstimated() {
		return estimated;
	}

	/**
	 * @param estimated
	 *            the estimated to set
	 */
	public void setEstimated(boolean estimated) {
		this.estimated = estimated;
	}

}
