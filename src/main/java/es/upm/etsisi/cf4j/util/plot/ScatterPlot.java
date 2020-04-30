package es.upm.etsisi.cf4j.util.plot;

import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.graphics.Insets2D;
import de.erichseifert.gral.graphics.Label;
import de.erichseifert.gral.plots.AbstractPlot;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.points.DefaultPointRenderer2D;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.util.GraphicsUtils;
import org.apache.commons.math3.util.Pair;

import java.awt.geom.Ellipse2D;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Implements an ScatterPlot. */
public class ScatterPlot extends Plot {

  /** List of points of the scatter plot */
  private List<Pair<Double, Double>> points;

  /** Label of the x axis */
  private String xLabel;

  /** Label of the y axis */
  private String yLabel;

  /**
   * Creates new ScatterPlot
   *
   * @param xLabel Label of the x axis
   * @param yLabel Label of the y axis
   */
  public ScatterPlot(String xLabel, String yLabel) {
    this.xLabel = xLabel;
    this.yLabel = yLabel;

    this.points = new ArrayList<>();
  }

  /**
   * Adds new point to the scatter plot
   *
   * @param x x value
   * @param y y value
   * @return Self ScatterPlot instance
   */
  public ScatterPlot addPoint(double x, double y) {
    this.points.add(new Pair<>(x, y));
    return this;
  }

  @Override
  protected String[] getDataHeaders() {
    String[] headers = {this.xLabel, this.yLabel};
    return headers;
  }

  @Override
  protected String[][] getDataContent(String xAxisTicksFormat, String yAxisTicksFormat) {
    DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
    DecimalFormat xdf = new DecimalFormat(xAxisTicksFormat, dfs);
    DecimalFormat ydf = new DecimalFormat(yAxisTicksFormat, dfs);

    String[][] content = new String[this.points.size()][2];

    for (int i = 0; i < this.points.size(); i++) {
      Pair<Double, Double> point = this.points.get(i);
      content[i][0] = xdf.format(point.getFirst());
      content[i][1] = ydf.format(point.getSecond());
    }

    return content;
  }

  @Override
  protected AbstractPlot getGralPlot() {

    // Create XY plot with data
    DataTable data = new DataTable(Double.class, Double.class);
    for (Pair<Double, Double> point : this.points) {
      data.add(point.getFirst(), point.getSecond());
    }

    DataSeries series = new DataSeries("Series", data);

    XYPlot plot = new XYPlot(series);

    // Customize plot
    plot.setBackground(PlotSettings.getBackgroundColor());

    PointRenderer pr = new DefaultPointRenderer2D();
    pr.setShape(new Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0));
    pr.setColor(GraphicsUtils.deriveWithAlpha(PlotSettings.getColor(0), 128));
    plot.setPointRenderers(series, pr);

    plot.setInsets(
        new Insets2D.Double(
            PlotSettings.getClearInset(),
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

    // Customize navigator settings
    plot.getNavigator().setZoom(0.9);
    plot.getNavigator().setZoomable(false);

    return plot;
  }
}
