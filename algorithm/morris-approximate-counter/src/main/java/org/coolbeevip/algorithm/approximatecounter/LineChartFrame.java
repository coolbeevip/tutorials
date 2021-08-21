package org.coolbeevip.algorithm.approximatecounter;

import java.awt.GraphicsConfiguration;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class LineChartFrame extends JFrame {

  final XYSeriesCollection dataset = new XYSeriesCollection();

  public LineChartFrame(String applicationTitle, String chartTitle, String xAxisLabel,
      String yAxisLabel) {
    super(applicationTitle);
    JFreeChart pieChart = ChartFactory
        .createXYLineChart(chartTitle,
            xAxisLabel,
            yAxisLabel,
            dataset,
            PlotOrientation.VERTICAL, true, true, false);

    ChartPanel chartPanel = new ChartPanel(pieChart);
    chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
    setContentPane(chartPanel);
  }

  public void addXYSeries(String label, double[][] values) {
    final XYSeries series = new XYSeries(label);
    for (int x = 0; x < values.length; x++) {
      series.add(values[x][0],values[x][1]);
    }
    dataset.addSeries(series);
  }

  private XYDataset createDataset() {

    final XYSeries firefox = new XYSeries("Firefox");
    firefox.add(1.0, 1.0);
    firefox.add(2.0, 3.0);
    firefox.add(3.0, 4.0);

    final XYSeries chrome = new XYSeries("Chrome");
    chrome.add(1.0, 4.0);
    chrome.add(2.0, 6.0);
    chrome.add(3.0, 5.0);

    final XYSeriesCollection dataset = new XYSeriesCollection();
    dataset.addSeries(firefox);
    dataset.addSeries(chrome);

    return dataset;

  }
}