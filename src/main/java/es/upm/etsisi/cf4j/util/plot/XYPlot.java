package es.upm.etsisi.cf4j.util.plot;

import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.graphics.Insets2D;
import de.erichseifert.gral.graphics.Label;
import de.erichseifert.gral.graphics.Location;
import de.erichseifert.gral.graphics.Orientation;
import de.erichseifert.gral.plots.AbstractPlot;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.plots.points.PointRenderer;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.*;

/** Implements an XYPlot. */
public class XYPlot extends Plot {

  /** Series data */
  private List<Triple<String, double[], double[]>> series;

  /** Labels related witch each xy point */
  private String[] pointsLabels;

  /** Series names that must show the points labels */
  private Set<String> hasLabelsVisible;

  /** Label of the x axis */
  private String xLabel;

  /** Label of the y axis */
  private String yLabel;

  /** Indicates if the legend must be hidden */
  private boolean hideLegend;

  /**
   * Creates a new XYPlot
   *
   * @param xLabel Label of the x axis
   * @param yLabel Label of the y axis
   */
  public XYPlot(String[] values, String xLabel, String yLabel) {
    this(values, xLabel, yLabel, false);
  }

  /**
   * Creates a new XYPlot
   *
   * @param xLabel Label of the x axis
   * @param yLabel Label of the y axis
   * @param hideLegend Indicates if the legend must be hidden. False by default.
   */
  public XYPlot(String[] values, String xLabel, String yLabel, boolean hideLegend) {
    this.pointsLabels = values;
    this.xLabel = xLabel;
    this.yLabel = yLabel;
    this.hideLegend = hideLegend;

    this.series = new ArrayList<>();
    this.hasLabelsVisible = new HashSet<>();
  }

  /**
   * Adds a new empty series to the plot. Points are initialized to x=0 and y=0 all point labels.
   *
   * @param seriesName Series name
   * @return Self XYPlot instance
   */
  public XYPlot addSeries(String seriesName) {
    return this.addSeries(seriesName, 0, 0);
  }

  /**
   * Adds a new series to the plot initializing x and y to a constant one for all point labels.
   *
   * @param seriesName Series name
   * @param x x initialization
   * @param y y initialization
   * @return Self XYPlot instance
   */
  public XYPlot addSeries(String seriesName, double x, double y) {
    double[] xs = new double[this.pointsLabels.length];
    Arrays.fill(xs, x);

    double[] ys = new double[this.pointsLabels.length];
    Arrays.fill(ys, y);

    return this.addSeries(seriesName, xs, ys);
  }

  /**
   * Adds a new series to the plot. xs and ys positions must be correlated with point labels.
   *
   * @param seriesName Series name
   * @param xs x values
   * @param ys y values
   * @return Self XYPlot instance
   */
  public XYPlot addSeries(String seriesName, double[] xs, double[] ys) {
    this.series.add(new MutableTriple(seriesName, xs, ys));
    return this;
  }

  /**
   * Sets a single point of a series
   *
   * @param seriesName Series name
   * @param label Label value of the point. Must exists
   * @param x x value
   * @param y y value
   * @return Self XYPlot instance
   */
  public XYPlot setXY(String seriesName, String label, double x, double y) {
    int labelIndex = 0;
    while (!this.pointsLabels[labelIndex].equals(label)) {
      labelIndex++;
    }

    int seriesIndex = 0;
    while (!this.series.get(seriesIndex).getLeft().equals(seriesName)) {
      seriesIndex++;
    }

    this.series.get(seriesIndex).getMiddle()[labelIndex] = x;
    this.series.get(seriesIndex).getRight()[labelIndex] = y;

    return this;
  }

  /**
   * Set the labels visible for a series
   *
   * @param seriesName Series name
   * @return Self XYPlot instance
   */
  public XYPlot setLabelsVisible(String seriesName) {
    this.hasLabelsVisible.add(seriesName);
    return this;
  }

  /**
   * Set the labels not visible for a series
   *
   * @param seriesName Series name
   * @return Self XYPlot instance
   */
  public XYPlot setLabelsNotVisible(String seriesName) {
    this.hasLabelsVisible.remove(seriesName);
    return this;
  }

  @Override
  protected String[] getDataHeaders() {
    String[] headers = new String[1 + 2 * this.series.size()];

    headers[0] = "Value";

    for (int i = 0; i < this.series.size(); i++) {
      Triple<String, double[], double[]> s = this.series.get(i);
      String seriesName = s.getLeft();
      headers[1 + i * 2] = seriesName + "(" + this.xLabel + ")";
      headers[2 + i * 2] = seriesName + " (" + this.yLabel + ")";
    }

    return headers;
  }

  @Override
  protected String[][] getDataContent(String xAxisTicksFormat, String yAxisTicksFormat) {
    DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
    DecimalFormat xdf = new DecimalFormat(xAxisTicksFormat, dfs);
    DecimalFormat ydf = new DecimalFormat(yAxisTicksFormat, dfs);

    String[][] content = new String[this.pointsLabels.length][1 + 2 * this.series.size()];

    for (int row = 0; row < this.pointsLabels.length; row++) {
      content[row][0] = this.pointsLabels[row];
      for (int i = 0; i < this.series.size(); i++) {
        Triple<String, double[], double[]> s = this.series.get(i);
        content[row][1 + i * 2] = xdf.format(s.getMiddle()[row]);
        content[row][2 + i * 2] = ydf.format(s.getRight()[row]);
      }
    }

    return content;
  }

  @Override
  protected AbstractPlot getGralPlot() {

    // Create XY plot with data
    de.erichseifert.gral.plots.XYPlot plot = new de.erichseifert.gral.plots.XYPlot();

    for (int i = 0; i < this.series.size(); i++) {
      String name = this.series.get(i).getLeft();
      double[] xs = this.series.get(i).getMiddle();
      double[] ys = this.series.get(i).getRight();

      DataTable data = new DataTable(Double.class, Double.class, String.class);
      for (int j = 0; j < this.pointsLabels.length; j++) {
        data.add(xs[j], ys[j], this.pointsLabels[j]);
      }

      DataSeries series = new DataSeries(name, data);
      plot.add(i, series, true);

      LineRenderer lineRenderer = new DefaultLineRenderer2D();
      lineRenderer.setColor(PlotSettings.getColor(i));
      plot.setLineRenderers(series, lineRenderer);

      PointRenderer pointRenderer = plot.getPointRenderers(series).get(0);
      pointRenderer.setColor(PlotSettings.getColor(i));
      pointRenderer.setValueVisible(this.hasLabelsVisible.contains(name));
      pointRenderer.setValueColumn(2);
      pointRenderer.setValueFont(PlotSettings.getSecondaryFont());
      pointRenderer.setValueAlignmentY(1);
      pointRenderer.setValueColor(PlotSettings.getColor(i));
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
    AxisRenderer xAxisRenderer = plot.getAxisRenderer(de.erichseifert.gral.plots.XYPlot.AXIS_X);

    xAxisRenderer.setLabel(new Label(xLabel));
    xAxisRenderer.getLabel().setFont(PlotSettings.getPrimaryFont());
    xAxisRenderer.setLabelDistance(PlotSettings.getxAxisLabelDistance());

    xAxisRenderer.setTickFont(PlotSettings.getSecondaryFont());
    xAxisRenderer.setTickLabelFormat(NumberFormat.getInstance(Locale.US));
    xAxisRenderer.setTicksAutoSpaced(true);

    // Customize y axis
    AxisRenderer yAxisRenderer = plot.getAxisRenderer(de.erichseifert.gral.plots.XYPlot.AXIS_Y);

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
    plot.getNavigator().setZoom(0.85);
    plot.getNavigator().setZoomable(false);

    return plot;
  }
}
