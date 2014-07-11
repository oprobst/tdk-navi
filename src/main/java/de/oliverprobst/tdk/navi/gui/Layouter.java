package de.oliverprobst.tdk.navi.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.Stroke;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.ui.RectangleInsets;

public class Layouter implements Serializable {

	/**
	 * sid
	 */
	private static final long serialVersionUID = -421773507429966278L;

	/**
	 * ctor
	 */
	Layouter() {

	}

	private static Color BACKGROUND = new Color(0);

	public void layout(Component c) {
		c.setBackground(BACKGROUND);
		c.setForeground(Color.WHITE);

	}

	public void layout(JFreeChart chart) {

		chart.setBorderVisible(false);
		// chart.setAntiAlias(false);
		chart.setPadding(new RectangleInsets(5, 0, 5, 0));
		chart.setBackgroundImage(getBlackPixel());
		// chart.getSubtitles().clear();
		chart.setBackgroundImageAlpha(1.0f);
	}

	public void layout(ChartPanel chartpanel) {
		chartpanel.setBackground(BACKGROUND);
	}

	public void layout(XYPlot plot) {
		plot.setBackgroundImageAlpha(1);
		plot.setBackgroundImage(getBlackPixel());

	}

	private Image getBlackPixel() {
		ClassLoader classloader = Thread.currentThread()
				.getContextClassLoader();
		InputStream is = classloader.getResourceAsStream("blackPx.png");

		try {
			return ImageIO.read(is);
		} catch (IOException ex) {
			throw new RuntimeException("Error loading demo map 'demoMap.png'.",
					ex);
		}
	}

	public void layout(JPanel panel) {

		 panel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		panel.setBackground(BACKGROUND);

	}

	private void layoutLabel(JLabel label) {
		// label.setBorder(BorderFactory.createLineBorder(Color.magenta));
		label.setBackground(BACKGROUND);
	}

	public void layoutMajorLabel(JLabel label) {
		this.layoutLabel(label);
		label.setForeground(Color.WHITE);
		label.setFont(label.getFont().deriveFont(28.0f));

	}

	public void layoutDescriptionLabel(JLabel label) {
		this.layoutLabel(label);
		label.setForeground(new Color(180, 180, 255));
	}

	public void layoutMicroLabel(JLabel label) {
		this.layoutLabel(label);
		label.setForeground(Color.WHITE);
	}

	public void layoutTinyDescriptionLabel(JLabel label) {
		this.layoutLabel(label);
		label.setFont(label.getFont().deriveFont(8.0f));
		label.setForeground(new Color(180, 180, 255));
	}
	
	public void layoutTinyLabel(JLabel label) {
		this.layoutLabel(label);
		label.setFont(label.getFont().deriveFont(8.0f));
		label.setForeground(Color.WHITE);
	}

	public void layout(NumberAxis axis) {
		axis.setTickLabelFont(axis.getTickLabelFont().deriveFont(20.0f));
		axis.setTickLabelInsets(new RectangleInsets(3, 0, 3, 0));
		axis.setTickLabelPaint(new Color(255, 50, 50));
		axis.setAutoRangeIncludesZero(true);
		axis.setAutoRangeMinimumSize(5);
		axis.setDefaultAutoRange(new Range(-5, 0));
		axis.setNumberFormatOverride(new DecimalFormat("#"));
	}

	public void layout(XYLineAndShapeRenderer renderer) {

		renderer.setSeriesItemLabelsVisible(0, false);
		renderer.setSeriesShapesVisible(0, false);
		renderer.setSeriesPaint(0, new Color(150, 150, 255));

		Stroke line = new BasicStroke(3, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 10.0f);
		renderer.setSeriesStroke(0, line);
	}

	public void layoutMinorLabel(JLabel label) {
		this.layoutLabel(label);
		label.setBackground(BACKGROUND);
		label.setForeground(Color.WHITE);
		label.setFont(label.getFont().deriveFont(17.0f));

	}

}
