package es.upm.etsisi.cf4j.util.plot;

import de.erichseifert.gral.data.*;
import de.erichseifert.gral.data.statistics.Histogram1D;
import de.erichseifert.gral.data.statistics.Statistics;
import de.erichseifert.gral.graphics.Insets2D;
import de.erichseifert.gral.graphics.Label;
import de.erichseifert.gral.graphics.Orientation;
import de.erichseifert.gral.plots.AbstractPlot;
import de.erichseifert.gral.plots.BarPlot;
import de.erichseifert.gral.plots.axes.AxisRenderer;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.*;

/** Implements a histogram plot. */
public class HistogramPlot extends Plot {

  /** Plot data */
  private DataTable data;

  /** Number of bins of the histogram */
  private int numBins;

  /** Label of the x axis */
  private String xLabel;

  /**
   * Creates a new HistogramPlot
   *
   * @param xLabel Label of the x axis
   * @param numBins Number of bins of the histogram
   */
  public HistogramPlot(String xLabel, int numBins) {
    this.xLabel = xLabel;
    this.numBins = numBins;
    this.data = new DataTable(Double.class);
  }

  /**
   * Adds new value to the histogram
   *
   * @param value Value
   * @return Self HistogramPlot instance
   */
  public HistogramPlot addValue(double value) {
    this.data.add(value);
    return this;
  }

  @Override
  public void printData() {
    this.printData("0.00", "0");
  }

  @Override
  public void printData(String axisTicksFormat) {
    this.printData(axisTicksFormat, "0");
  }

  @Override
  public String toString() {
    return this.toString("0.00", "0");
  }

  @Override
  public String toString(String axisTicksFormat) {
    return this.toString(axisTicksFormat, "0");
  }

  @Override
  protected String[] getDataHeaders() {
    String[] headers = {this.xLabel + " (From)", this.xLabel + " (To)", "Count"};
    return headers;
  }

  @Override
  protected String[][] getDataContent(String xAxisTicksFormat, String yAxisTicksFormat) {
    double minValue = this.data.getStatistics().get(Statistics.MIN);
    double maxValue = this.data.getStatistics().get(Statistics.MAX);
    double barWidth = (maxValue - minValue) / this.numBins;

    Histogram1D h1d = new Histogram1D(this.data, Orientation.VERTICAL, this.numBins);
    DataSource histogram = new EnumeratedData(h1d, minValue + barWidth / 2, barWidth);

    DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);

    DecimalFormat xdf =
        new DecimalFormat(
            (minValue < 0) ? "+" + xAxisTicksFormat + ";-" + xAxisTicksFormat : xAxisTicksFormat,
            dfs);

    DecimalFormat ydf = new DecimalFormat(yAxisTicksFormat, dfs);

    String[][] content = new String[histogram.getRowCount()][3];

    for (int r = 0; r < histogram.getRowCount(); r++) {
      Row row = histogram.getRow(r);

      double from = (Double) row.get(0) - barWidth / 2;
      double to = (Double) row.get(0) + barWidth / 2;
      long count = (Long) row.get(1);

      content[r][0] = xdf.format(from);
      content[r][1] = xdf.format(to);
      content[r][2] = ydf.format(count);
    }

    return content;
  }

  /**
   * Generates a blank String of fixed length
   *
   * @param length Length of the string
   * @return Blank String
   */
  private String blankString(int length) {
    StringBuilder str = new StringBuilder();
    while (length > 0) {
      str.append(" ");
      length--;
    }
    return str.toString();
  }

  @Override
  protected AbstractPlot getGralPlot() {
    // Create histogram plot
    double minValue = this.data.getStatistics().get(Statistics.MIN);
    double maxValue = this.data.getStatistics().get(Statistics.MAX);
    double barWidth = (maxValue - minValue) / this.numBins;

    double displayOffset = 10 + Math.abs(minValue); // Hack to avoid visualization of 0 tick label

    Histogram1D h1d = new Histogram1D(this.data, Orientation.VERTICAL, this.numBins);
    DataSource histogram =
        new EnumeratedData(h1d, displayOffset + minValue + barWidth / 2, barWidth);

    BarPlot plot = new BarPlot(histogram);

    // Customize plot
    plot.setBackground(PlotSettings.getBackgroundColor());
    plot.setBorderColor(PlotSettings.getBackgroundColor());
    plot.setBarWidth(barWidth);

    plot.setInsets(
        new Insets2D.Double(
            PlotSettings.getClearInset(),
            PlotSettings.getyAxisInset(),
            PlotSettings.getxAxisInset(),
            PlotSettings.getClearInset()));

    BarPlot.BarRenderer barRenderer =
        (BarPlot.BarRenderer) plot.getPointRenderers(histogram).get(0);
    barRenderer.setColor(PlotSettings.getColor(0));
    barRenderer.setBorderColor(PlotSettings.getBackgroundColor());
    barRenderer.setBorderStroke(new BasicStroke(1));

    // Customize x axis
    AxisRenderer xAxisRenderer = plot.getAxisRenderer(BarPlot.AXIS_X);

    xAxisRenderer.setLabel(new Label(xLabel));
    xAxisRenderer.getLabel().setFont(PlotSettings.getPrimaryFont());
    xAxisRenderer.setLabelDistance(PlotSettings.getxAxisLabelDistance());

    xAxisRenderer.setTickFont(PlotSettings.getSecondaryFont());
    xAxisRenderer.setTickSpacing(Double.MAX_VALUE); // Hack to avoid default tick labels
    xAxisRenderer.setMinorTicksVisible(false);
    xAxisRenderer.setTickAlignment(0);
    xAxisRenderer.setTickLength(xAxisRenderer.getTickLength() / 2.0);
    xAxisRenderer.setIntersection(0);

    Map<Double, String> xTicks = new HashMap<>();
    DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));
    for (int i = 0; i < this.numBins + 1; i++) {
      double tick = minValue + i * barWidth;
      xTicks.put(displayOffset + tick, df.format(tick));
    }
    xAxisRenderer.setCustomTicks(xTicks);

    double xMin = displayOffset + minValue - (maxValue - minValue) / 1000;
    double xMax = displayOffset + maxValue + (maxValue - minValue) / 1000;
    plot.getAxis(BarPlot.AXIS_X).setRange(xMin, xMax);

    // Customize y axis
    AxisRenderer yAxisRenderer = plot.getAxisRenderer(BarPlot.AXIS_Y);

    yAxisRenderer.setLabel(new Label("Count"));
    yAxisRenderer.getLabel().setFont(PlotSettings.getPrimaryFont());
    yAxisRenderer.getLabel().setRotation(90);
    yAxisRenderer.setLabelDistance(PlotSettings.getyAxisLabelDistance());

    yAxisRenderer.setTickFont(PlotSettings.getSecondaryFont());
    yAxisRenderer.setTickLabelFormat(NumberFormat.getInstance(Locale.US));
    yAxisRenderer.setTicksAutoSpaced(true);
    yAxisRenderer.setIntersection(displayOffset + minValue);

    double yMax = histogram.getStatistics().get(Statistics.MAX, Orientation.VERTICAL, 1) * 1.05;
    plot.getAxis(BarPlot.AXIS_Y).setRange(0, yMax);

    // Customize navigator settings
    plot.getNavigator().setZoomable(true);

    return plot;
  }
}
