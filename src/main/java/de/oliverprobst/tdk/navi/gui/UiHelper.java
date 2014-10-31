package de.oliverprobst.tdk.navi.gui;

import javax.swing.ImageIcon;

/**
 * Functions used multiple times.
 */
public final class UiHelper {

	/** Returns an ImageIcon, or exception if the path was invalid. */
	public static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = UiHelper.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			throw new RuntimeException("Failed to load image at " + path);

		}
	}

	private UiHelper() {
	}
}
