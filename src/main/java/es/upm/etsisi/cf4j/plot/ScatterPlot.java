package es.upm.etsisi.cf4j.plot;

import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.graphics.Insets2D;
import de.erichseifert.gral.graphics.Label;
import de.erichseifert.gral.io.plots.DrawableWriter;
import de.erichseifert.gral.io.plots.DrawableWriterFactory;
import de.erichseifert.gral.plots.AbstractPlot;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.points.DefaultPointRenderer2D;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.util.GraphicsUtils;
import org.apache.commons.math3.util.Pair;

import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class ScatterPlot extends Plot {

    private List<Pair<Double, Double>> points;

    private String xLabel;

    private String yLabel;

    public ScatterPlot(String xLabel, String yLabel) {
        this.xLabel = xLabel;
        this.yLabel = yLabel;

        this.points = new ArrayList<>();
    }

    public void addPoint(double x, double y) {
        this.points.add(new Pair<>(x, y));
    }

    @Override
    protected String getCSVHeader(String separator) {
        return "\"" + this.xLabel + "\"" + separator + "\"" + this.yLabel + "\"";
    }

    @Override
    protected Iterator<String> getCSVContent(String separator) {
        List<String> content = new ArrayList<>();
        for (Pair<Double, Double> point : this.points) {
            content.add(point.getFirst() + separator + point.getSecond());
        }
        return content.iterator();
    }

    @Override
    public String toString(String xAxisTicksFormat, String yAxisTicksFormat) {
        DecimalFormat xdf = new DecimalFormat(xAxisTicksFormat);
        DecimalFormat ydf = new DecimalFormat(yAxisTicksFormat);

        StringBuilder sb = new StringBuilder();

        sb.append(this.xLabel).append(this.blankString(Math.max(0, xAxisTicksFormat.length() - this.xLabel.length())));
        sb.append("  ");
        sb.append(this.yLabel).append(this.blankString(Math.max(0, yAxisTicksFormat.length() - this.yLabel.length())));

        for (Pair<Double, Double> point : this.points) {
            sb.append("\n");
            sb.append(xdf.format(point.getFirst())).append(this.blankString(Math.max(0, xAxisTicksFormat.length() - this.xLabel.length())));
            sb.append("  ");
            sb.append(ydf.format(point.getSecond())).append(this.blankString(Math.max(0, yAxisTicksFormat.length() - this.yLabel.length())));
        }

        return sb.toString();
    }

    /**
     * Generates a blank String of fixed length
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

        plot.setInsets(new Insets2D.Double(
                PlotSettings.getClearInset(),
                PlotSettings.getyAxisInset(),
                PlotSettings.getxAxisInset(),
                PlotSettings.getClearInset()
        ));

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
