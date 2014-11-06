package de.oliverprobst.tdk.navi.gui;

import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.util.Map;

import javax.swing.JPanel;

/**
 * Abstract JPanel provide helper functions for all panels used.
 *
 * 
 * @author <b></b>www.tief-dunkel-kalt.org</b><br>
 *         Oliver Probst <a
 *         href="mailto:oliverprobst@gmx.de">oliverprobst@gmx.de</a>
 * 
 */
public class AbstractNaviJPanel extends JPanel {

	/**
	 * suid
	 */
	private static final long serialVersionUID = 4379139566953648814L;

	/**
	 * Instantiates a new abstract navi JPanel.
	 */
	public AbstractNaviJPanel() {
		super();
	}

	/**
	 * Instantiates a new navi JPanel.
	 *
	 * @param layout
	 *            the layout manager to use
	 */
	public AbstractNaviJPanel(LayoutManager layout) {
		super(layout);
	}

	/**
	 * Define default rendering hints to be used by several panels.
	 *
	 * @return Map with rendering hints according to awt api.
	 */
	protected Map<?, ?> defineRenderingHints() {
		RenderingHints rh = new RenderingHints(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		rh.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		rh.put(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);

		rh.put(RenderingHints.KEY_COLOR_RENDERING,
				RenderingHints.VALUE_COLOR_RENDER_SPEED);
		rh.put(RenderingHints.KEY_DITHERING,
				RenderingHints.VALUE_DITHER_DISABLE);
		rh.put(RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
		rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		rh.put(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);

		return rh;
	}

}