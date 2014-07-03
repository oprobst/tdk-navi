package de.oliverprobst.tdk.navi.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oliverprobst.tdk.navi.controller.DiveDataProperties;
import de.oliverprobst.tdk.navi.dto.DiveData;

public class DiveProfilePanel extends JPanel implements PropertyChangeListener {

	/**
	 * sid
	 */
	private static final long serialVersionUID = 2263251613504203833L;

	XYSeriesCollection dataset = new XYSeriesCollection();
	XYSeries divedata = new XYSeries("Tiefe");

	private Logger log = LoggerFactory.getLogger(DiveProfilePanel.class);

	/**
	 * ctor
	 */
	public DiveProfilePanel(Layouter layout) {

		dataset.addSeries(divedata);
		divedata.add(0, 0);

		layout.layout(this);

		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);

		GridBagConstraints gbc = new GridBagConstraints(0, 0, 2, 1, 1.0d, 1.0d,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 2, 2);

		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		layout.layout(renderer);

		renderer.setBaseSeriesVisibleInLegend(false);
		NumberAxis xax = new NumberAxis("");
		NumberAxis yax = new NumberAxis("");
		xax.setVisible(false);
		layout.layout(yax);

		XYPlot plot = new XYPlot(dataset, xax, yax, renderer);

		layout.layout(plot);

		JFreeChart chart = new JFreeChart(plot);
		layout.layout(chart);

		ChartPanel chartPanel = new ChartPanel(chart);
		layout.layout(chartPanel);

		this.add(chartPanel, gbc);

	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(DiveDataProperties.PROP_UPDATEPROFILE)) {
			paintDiagram((List<DiveData>) evt.getNewValue());
		}

	}

	private void paintDiagram(List<DiveData> list) {

		int unbufferedEntries = list.size() - divedata.getItemCount();
		log.trace("unbuffered entries " + unbufferedEntries);

		for (int i = divedata.getItemCount(); i < list.size(); i++) {
			DiveData data = list.get(i);
			float depth = data.getDepth();
			long time = data.getSurfacetime() + data.getDivetime();
			divedata.add(time, (int) Math.round(depth * -1));
		}

	}
}
