package es.upm.etsisi.cf4j.util.plot;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.statistics.Statistics;
import de.erichseifert.gral.graphics.Insets2D;
import de.erichseifert.gral.graphics.Label;
import de.erichseifert.gral.graphics.Orientation;
import de.erichseifert.gral.plots.AbstractPlot;
import de.erichseifert.gral.plots.BarPlot;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import org.apache.commons.math3.util.Pair;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

/** Implements a column plot. */
public class ColumnPlot extends Plot {

  /** Default column width */
  private static final double COLUMN_WIDTH = 0.8;

  /** Columns to be showed in the plot */
  private List<Pair<String, Double>> columns;

  /** Label of the x axis */
  private String xLabel;

  /** label of the y axis */
  private String yLabel;

  /**
   * Creates a new ColumnPlot
   *
   * @param xLabel Label of the x axis
   * @param yLabel Label of the y axis
   */
  public ColumnPlot(String xLabel, String yLabel) {
    this.xLabel = xLabel;
    this.yLabel = yLabel;
    this.columns = new ArrayList<>();
  }

  /**
   * Adds a column to the plot
   *
   * @param name Column name
   * @param value Column value
   * @return Self ColumnPlot instance
   */
  public ColumnPlot addColumn(String name, double value) {
    this.columns.add(new Pair(name, value));
    return this;
  }

  @Override
  protected String[] getDataHeaders() {
    String[] headers = {this.xLabel, this.yLabel};
    return headers;
  }

  @Override
  protected String[][] getDataContent(String xAxisTicksFormat, String yAxisTicksFormat) {
    DecimalFormat ydf = new DecimalFormat(yAxisTicksFormat, new DecimalFormatSymbols(Locale.US));

    String[][] content = new String[this.columns.size()][2];

    for (int i = 0; i < this.columns.size(); i++) {
      Pair<String, Double> column = this.columns.get(i);

      String name = column.getKey();
      content[i][0] = name;

      double value = column.getValue();
      content[i][1] = ydf.format(value);
    }

    return content;
  }

  @Override
  protected AbstractPlot getGralPlot() {

    // Create column plot data
    DataTable data = new DataTable(Double.class, Double.class);
    for (int i = 0; i < this.columns.size(); i++) {
      Pair<String, Double> column = this.columns.get(i);
      double x = 1.5 + i;
      data.add(x, column.getValue());
    }

    BarPlot plot = new BarPlot(data);

    // Customize plot
    plot.setBackground(PlotSettings.getBackgroundColor());
    plot.setBorderColor(PlotSettings.getBackgroundColor());
    plot.setBarWidth(COLUMN_WIDTH);

    plot.setInsets(
        new Insets2D.Double(
            PlotSettings.getClearInset(),
            PlotSettings.getyAxisInset(),
            PlotSettings.getxAxisInset(),
            PlotSettings.getClearInset()));

    BarPlot.BarRenderer barRenderer = (BarPlot.BarRenderer) plot.getPointRenderers(data).get(0);
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
    for (int i = 0; i < this.columns.size(); i++) {
      Pair<String, Double> column = this.columns.get(i);
      xTicks.put(1.5 + i, column.getKey());
    }
    xAxisRenderer.setCustomTicks(xTicks);

    plot.getAxis(BarPlot.AXIS_X).setRange(1.0, 1.0 + this.columns.size());

    // Customize y axis
    AxisRenderer yAxisRenderer = plot.getAxisRenderer(BarPlot.AXIS_Y);

    yAxisRenderer.setLabel(new Label(this.yLabel));
    yAxisRenderer.getLabel().setFont(PlotSettings.getPrimaryFont());
    yAxisRenderer.getLabel().setRotation(90);
    yAxisRenderer.setLabelDistance(PlotSettings.getyAxisLabelDistance());

    yAxisRenderer.setTickFont(PlotSettings.getSecondaryFont());
    yAxisRenderer.setTickLabelFormat(NumberFormat.getInstance(Locale.US));
    yAxisRenderer.setTicksAutoSpaced(true);
    yAxisRenderer.setIntersection(1.0);

    double yMax = data.getStatistics().get(Statistics.MAX, Orientation.VERTICAL, 1) * 1.05;
    plot.getAxis(BarPlot.AXIS_Y).setRange(0, yMax);

    // Customize navigator settings
    plot.getNavigator().setZoomable(true);

    return plot;
  }
}
