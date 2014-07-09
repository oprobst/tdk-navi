package de.oliverprobst.tdk.navi.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.dial.DialBackground;
import org.jfree.chart.plot.dial.DialCap;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.StandardDialFrame;
import org.jfree.chart.plot.dial.StandardDialRange;
import org.jfree.chart.plot.dial.StandardDialScale;
import org.jfree.data.general.DefaultValueDataset;

import de.oliverprobst.tdk.navi.controller.DiveDataProperties;

public class SpeedPanel extends JPanel implements PropertyChangeListener {

	/**
	 * sid
	 */
	private static final long serialVersionUID = -5013078838389083700L;

	/**
	 * ctor
	 */
	public SpeedPanel(Layouter layout) {
		layout.layout(this);

		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);

		GridBagConstraints gbc = new GridBagConstraints(0, 0, 2, 1, 0.0d, 0.0d,
				GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
				new Insets(0, 0, 0, 0), 2, 2);

		ChartPanel panel = createDial(layout);
		this.add(panel, gbc);

		
		gbc = new GridBagConstraints(1, 1, 1, 1, 0.0d, 0.0d,
		GridBagConstraints.SOUTHEAST, GridBagConstraints.VERTICAL, new
		 Insets(0, 0, 0, 3), 2, 2);
		 
		JLabel lblDesc = new JLabel("Speed");
		layout.layoutTinyDescriptionLabel(lblDesc); this.add(lblDesc, gbc);
		
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(DiveDataProperties.PROP_SPEED)) {
			Integer value = (Integer) evt.getNewValue();
			dataset.setValue(value);
		}
	}

	DefaultValueDataset dataset = new DefaultValueDataset(10D);

	private ChartPanel createDial(Layouter layout) {
		dataset.setValue(0);
		DialPlot dialplot = new DialPlot();
		dialplot.setDataset(dataset);
		StandardDialFrame frame = new StandardDialFrame();
		frame.setBackgroundPaint(Color.WHITE);
		frame.setForegroundPaint(Color.LIGHT_GRAY);
		dialplot.setDialFrame(frame);
		dialplot.setBackground(new DialBackground());

		StandardDialScale standarddialscale = new StandardDialScale(0, 9,
				-120D, -300D, 1, 0);

		standarddialscale.setTickRadius(0.88D);
		standarddialscale.setTickLabelOffset(0.14999999999999999D);

		standarddialscale.setTickLabelFont(new Font("Dialog", Font.BOLD, 25));
		standarddialscale.setTickLabelPaint(Color.WHITE);
		standarddialscale.setTickLabelFormatter(new DecimalFormat("#0"));
		dialplot.addScale(0, standarddialscale);
		dialplot.addPointer(new org.jfree.chart.plot.dial.DialPointer.Pin());

		DialCap dialcap = new DialCap();
		dialplot.setCap(dialcap);

		JFreeChart jfreechart = new JFreeChart(dialplot);

		StandardDialRange standarddialrange = new StandardDialRange(7D, 9D,
				Color.red);
		standarddialrange.setInnerRadius(0.52000000000000002D);
		standarddialrange.setOuterRadius(0.55000000000000004D);
		dialplot.addLayer(standarddialrange);
		StandardDialRange standarddialrange1 = new StandardDialRange(2D, 7D,
				Color.green);
		standarddialrange1.setInnerRadius(0.52000000000000002D);
		standarddialrange1.setOuterRadius(0.55000000000000004D);
		dialplot.addLayer(standarddialrange1);
		StandardDialRange standarddialrange2 = new StandardDialRange(0D, 2D,
				Color.orange);
		standarddialrange2.setInnerRadius(0.52000000000000002D);
		standarddialrange2.setOuterRadius(0.55000000000000004D);
		dialplot.addLayer(standarddialrange2);

		DialBackground dialbackground = new DialBackground(Color.BLACK);
		dialplot.setBackground(dialbackground);

		dialplot.removePointer(0);
		org.jfree.chart.plot.dial.DialPointer.Pointer pointer = new org.jfree.chart.plot.dial.DialPointer.Pointer();
		pointer.setFillPaint(Color.RED);
		dialplot.addPointer(pointer);

		ChartPanel chartpanel = new ChartPanel(jfreechart);
		chartpanel.setPreferredSize(new Dimension(60, 60));
		chartpanel.setBackground(Color.BLACK);
		jfreechart.setBackgroundPaint(Color.BLACK);
		return chartpanel;
	}

}
