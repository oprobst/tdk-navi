package de.oliverprobst.tdk.navi.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * 
 * Image dimension: 480, 360
 */
public class MapPanel extends JPanel implements PropertyChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1775948240481194745L;

	private BufferedImage image;

	public MapPanel() {
		 
		ClassLoader classloader = Thread.currentThread()
				.getContextClassLoader();
		InputStream is = classloader.getResourceAsStream("demoMap.png");

		try {
			image = ImageIO.read(is);
		} catch (IOException ex) {
			throw new RuntimeException("Error loading demo map 'demoMap.png'.",
					ex);
		}
		this.setPreferredSize(new Dimension (480,360)); 
		 
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 0, 0, null);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		// todo paint location

	}

}
