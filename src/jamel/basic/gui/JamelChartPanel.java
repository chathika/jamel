package jamel.basic.gui;

import java.awt.Color;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.ValueMarker;

/**
 * A convenient extension of ChartPanel.
 */
public class JamelChartPanel extends ChartPanel {

	/** background */
	private static final Color background = JamelColor.getColor("background");

	/** A flag that indicates if the chart is a time chart or not. */
	private final boolean isTimeChart;

	/**
	 * Constructs a panel that displays the specified chart.
	 * @param chart the chart to be displayed.
	 * @param isTimeChart a flag that indicates if the chart is a scatter chart or not.
	 */
	public JamelChartPanel(JFreeChart chart, boolean isTimeChart) {
		super(chart);
		this.isTimeChart = isTimeChart;
		this.setBackground(background);
	}
	
	/**
	 * Adds a marker to the chart.
	 * @param marker the marker to be added.
	 */
	public void addMarker(ValueMarker marker) {
		if (isTimeChart) {
			this.getChart().getXYPlot().addDomainMarker(marker);
		}
	}

}

// ***
