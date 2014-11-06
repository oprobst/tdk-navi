package de.oliverprobst.tdk.navi.gui;

import java.awt.Color;

/**
 * Provide color constants for drawing the map.
 * 
 * 
 * @author <b></b>www.tief-dunkel-kalt.org</b><br>
 *         Oliver Probst <a
 *         href="mailto:oliverprobst@gmx.de">oliverprobst@gmx.de</a>
 * 
 */
public class MapPanelColors {

	private final boolean brightColorRoute;

	private final Color brightNewEstimated = new Color(90, 90, 255);

	private final Color brightNewReal = new Color(255, 90, 90);

	private final Color brightOldEstimated = new Color(90, 255, 90);

	private final Color brightOldReal = new Color(255, 90, 255);

	private final Color darkNewEstimated = new Color(0, 0, 255);

	private final Color darkNewReal = new Color(255, 0, 0);

	private final Color darkOldEstimated = new Color(150, 50, 255);

	private final Color darkOldReal = new Color(255, 100, 50);

	/**
	 * Instantiates a new map panel colors.
	 *
	 * @param brightColorRoute
	 *            Shall the route be drawn in bright or dark colors.
	 */
	public MapPanelColors(boolean brightColorRoute) {
		this.brightColorRoute = brightColorRoute;
	}

	/**
	 * Gets the route color.
	 *
	 * @param estimated
	 *            Distinguish between estimated and measured location
	 * @param isNew
	 *            indicator if the part shall be drawn as a newly detected or
	 *            old route (older parts will be 'greyed' out)
	 * @return the route color defined by the constants of this class'
	 */
	public Color getRouteColor(boolean estimated, boolean isNew) {
		if (this.brightColorRoute) {
			if (estimated) {
				if (isNew) {
					return brightNewEstimated;
				} else {
					return brightOldEstimated;
				}
			} else {
				if (isNew) {
					return brightNewReal;
				} else {
					return brightOldReal;
				}
			}

		} else {
			if (estimated) {
				if (isNew) {
					return darkNewEstimated;
				} else {
					return darkOldEstimated;
				}
			} else {
				if (isNew) {
					return darkNewReal;
				} else {
					return darkOldReal;
				}
			}
		}
	}
}
