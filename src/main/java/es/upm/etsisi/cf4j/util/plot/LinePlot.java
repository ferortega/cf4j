package es.upm.etsisi.cf4j.util.plot;

import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.graphics.Insets2D;
import de.erichseifert.gral.graphics.Label;
import de.erichseifert.gral.graphics.Location;
import de.erichseifert.gral.graphics.Orientation;
import de.erichseifert.gral.plots.AbstractPlot;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import org.apache.commons.math3.util.Pair;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

/** Implements a LinePlot. */
public class LinePlot extends Plot {

  /** Series data */
  private List<Pair<String, double[]>> series;

  /** Values of the x axis */
  private double[] xs;

  /** Label of the x axis */
  private String xLabel;

  /** Label of the y axis */
  private String yLabel;

  /** Indicates if the legend must be hidden */
  private boolean hideLegend;

  /**
   * Creates a new LinePlot
   *
   * @param xs Values of the x axis
   * @param xLabel Label of the x axis
   * @param yLabel Label of the y axis
   */
  public LinePlot(int[] xs, String xLabel, String yLabel) {
    this(Arrays.stream(xs).asDoubleStream().toArray(), xLabel, yLabel, false);
  }

  /**
   * Creates a new LinePlot
   *
   * @param xs Values of the x axis
   * @param xLabel Label of the x axis
   * @param yLabel Label of the y axis
   * @param hideLegend Indicates if the legend must be hidden. False by default.
   */
  public LinePlot(int[] xs, String xLabel, String yLabel, boolean hideLegend) {
    this(Arrays.stream(xs).asDoubleStream().toArray(), xLabel, yLabel, hideLegend);
  }

  /**
   * Creates a new LinePlot
   *
   * @param xs Values of the x axis
   * @param xLabel Label of the x axis
   * @param yLabel Label of the y axis
   */
  public LinePlot(double[] xs, String xLabel, String yLabel) {
    this(xs, xLabel, yLabel, false);
  }

  /**
   * Creates a new LinePlot
   *
   * @param xs Values of the x axis
   * @param xLabel Label of the x axis
   * @param yLabel Label of the y axis
   * @param hideLegend Indicates if the legend must be hidden. False by default.
   */
  public LinePlot(double[] xs, String xLabel, String yLabel, boolean hideLegend) {
    this.xs = xs;
    this.xLabel = xLabel;
    this.yLabel = yLabel;
    this.hideLegend = hideLegend;

    this.series = new ArrayList<>();
  }

  /**
   * Adds a new empty series to the plot. All series values are initialized to 0.
   *
   * @param seriesName Series name
   * @return Self LinePlot instance
   */
  public LinePlot addSeries(String seriesName) {
    return this.addSeries(seriesName, 0);
  }

  /**
   * Adds a new series to the plot initializing all the values to a constant one.
   *
   * @param seriesName Series name
   * @param value Constant value
   * @return Self LinePlot instance
   */
  public LinePlot addSeries(String seriesName, double value) {
    double[] values = new double[this.xs.length];
    Arrays.fill(values, value);
    this.addSeries(seriesName, values);
    return this;
  }

  /**
   * Adds a new series to the plot. y values positions must be correlated with xs values.
   *
   * @param seriesName Series name
   * @param y Values
   * @return Self LinePlot instance
   */
  public LinePlot addSeries(String seriesName, double[] y) {
    this.series.add(new Pair(seriesName, y));
    return this;
  }

  /**
   * Sets a single value of a series
   *
   * @param seriesName Series name
   * @param x x value. Must exists in xs.
   * @param y y value
   * @return Self LinePlot instance
   */
  public LinePlot setValue(String seriesName, int x, double y) {
    return this.setValue(seriesName, (double) x, y);
  }

  /**
   * Sets a single value of a series
   *
   * @param seriesName Series name
   * @param x x value. Must exists in xs.
   * @param y y value
   * @return Self LinePlot instance
   */
  public LinePlot setValue(String seriesName, double x, double y) {
    int xIndex = 0;
    while (this.xs[xIndex] != x) {
      xIndex++;
    }

    int seriesIndex = 0;
    while (!this.series.get(seriesIndex).getKey().equals(seriesName)) {
      seriesIndex++;
    }

    this.series.get(seriesIndex).getValue()[xIndex] = y;

    return this;
  }

  @Override
  protected String[] getDataHeaders() {
    String[] headers = new String[1 + this.series.size()];

    headers[0] = this.xLabel;

    for (int i = 0; i < this.series.size(); i++) {
      Pair<String, double[]> s = this.series.get(i);
      String seriesName = s.getKey();
      headers[i + 1] = seriesName;
    }

    return headers;
  }

  @Override
  protected String[][] getDataContent(String xAxisTicksFormat, String yAxisTicksFormat) {
    DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
    DecimalFormat xdf = new DecimalFormat(xAxisTicksFormat, dfs);
    DecimalFormat ydf = new DecimalFormat(yAxisTicksFormat, dfs);

    String[][] content = new String[this.xs.length][1 + this.series.size()];

    for (int row = 0; row < this.xs.length; row++) {
      content[row][0] = xdf.format(this.xs[row]);
      for (int i = 0; i < this.series.size(); i++) {
        Pair<String, double[]> s = this.series.get(i);
        double[] ys = s.getValue();
        content[row][i + 1] = ydf.format(ys[row]);
      }
    }

    return content;
  }

  @Override
  protected AbstractPlot getGralPlot() {

    // Create XY plot with data
    XYPlot plot = new XYPlot();

    for (int i = 0; i < this.series.size(); i++) {
      String name = this.series.get(i).getKey();
      double[] ys = this.series.get(i).getValue();

      DataTable data = new DataTable(Double.class, Double.class);
      for (int j = 0; j < this.xs.length; j++) {
        data.add(xs[j], ys[j]);
      }

      DataSeries series = new DataSeries(name, data);
      plot.add(i, series, true);
      plot.setLineRenderers(series, new DefaultLineRenderer2D());
      plot.getPointRenderers(series).get(0).setColor(PlotSettings.getColor(i));
      plot.getLineRenderers(series).get(0).setColor(PlotSettings.getColor(i));
    }

    // Customize plot
    plot.setBackground(PlotSettings.getBackgroundColor());

    plot.setInsets(
        new Insets2D.Double(
            (this.hideLegend) ? PlotSettings.getClearInset() : PlotSettings.getLegendInset(),
            PlotSettings.getyAxisInset(),
            PlotSettings.getxAxisInset(),
            PlotSettings.getClearInset()));

    // Customize x axis
    AxisRenderer xAxisRenderer = plot.getAxisRenderer(XYPlot.AXIS_X);

    xAxisRenderer.setLabel(new Label(xLabel));
    xAxisRenderer.getLabel().setFont(PlotSettings.getPrimaryFont());
    xAxisRenderer.setLabelDistance(PlotSettings.getxAxisLabelDistance());

    xAxisRenderer.setTickFont(PlotSettings.getSecondaryFont());
    xAxisRenderer.setTickLabelFormat(NumberFormat.getInstance(Locale.US));
    xAxisRenderer.setTicksAutoSpaced(true);

    // Customize y axis
    AxisRenderer yAxisRenderer = plot.getAxisRenderer(XYPlot.AXIS_Y);

    yAxisRenderer.setLabel(new Label(yLabel));
    yAxisRenderer.getLabel().setFont(PlotSettings.getPrimaryFont());
    yAxisRenderer.getLabel().setRotation(90);
    yAxisRenderer.setLabelDistance(PlotSettings.getyAxisLabelDistance());

    yAxisRenderer.setTickFont(PlotSettings.getSecondaryFont());
    yAxisRenderer.setTickLabelFormat(NumberFormat.getInstance(Locale.US));
    yAxisRenderer.setTicksAutoSpaced(true);

    xAxisRenderer.setIntersection(-Double.MAX_VALUE);
    yAxisRenderer.setIntersection(-Double.MAX_VALUE);

    // Customize legend
    if (!this.hideLegend) {
      plot.setLegendLocation(Location.NORTH);
      plot.setLegendVisible(true);
      plot.setLegendDistance(PlotSettings.getLegendDistance());
      plot.getLegend().setBorderStroke(null);
      plot.getLegend().setOrientation(Orientation.HORIZONTAL);
      plot.getLegend().setAlignmentX(0.5);
      plot.getLegend().setFont(PlotSettings.getPrimaryFont());
    }

    // Customize navigator settings
    plot.getNavigator().setZoom(0.9);
    plot.getNavigator().setZoomable(false);

    return plot;
  }
}
