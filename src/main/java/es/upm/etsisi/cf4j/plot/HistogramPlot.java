package es.upm.etsisi.cf4j.plot;

import de.erichseifert.gral.data.*;
import de.erichseifert.gral.data.statistics.Histogram1D;
import de.erichseifert.gral.data.statistics.Statistics;
import de.erichseifert.gral.graphics.Insets2D;
import de.erichseifert.gral.graphics.Label;
import de.erichseifert.gral.graphics.Location;
import de.erichseifert.gral.graphics.Orientation;
import de.erichseifert.gral.io.plots.DrawableWriter;
import de.erichseifert.gral.io.plots.DrawableWriterFactory;
import de.erichseifert.gral.plots.BarPlot;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import org.apache.commons.math3.util.Pair;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.*;

public class HistogramPlot extends Plot {

    private DataTable data;

    private int numBuckets;

    private String xLabel;

    public HistogramPlot(String xLabel, int numBuckets) {
        this.xLabel = xLabel;
        this.numBuckets = numBuckets;
        this.data = new DataTable(Double.class);
    }

    public void addValue(double value) {
        this.data.add(value);
    }


    @Override
    public void exportData(String filename, String separator) throws IOException {
        File f = new File(filename);
        File parent = f.getAbsoluteFile().getParentFile();
        if(!parent.exists() && !parent.mkdirs()) {
            throw new IOException("Unable to create directory "+ parent);
        }

        PrintWriter writer = new PrintWriter(f);

        double minValue = this.data.getStatistics().get(Statistics.MIN);
        double maxValue = this.data.getStatistics().get(Statistics.MAX);
        double barWidth = (maxValue - minValue) / this.numBuckets;

        Histogram1D h1d = new Histogram1D(this.data, Orientation.VERTICAL, this.numBuckets);
        DataSource histogram = new EnumeratedData(h1d, minValue + barWidth / 2, barWidth);

        writer.println("\"From\"" + separator + "\"To\"" + separator + "\"Count\"");

        for (int r = 0; r < histogram.getRowCount(); r++) {
            Row row = histogram.getRow(r);

            double from = (Double) row.get(0) - barWidth / 2;
            double to = (Double) row.get(0) + barWidth / 2;
            long count = (Long) row.get(1);

            writer.println(from + separator + to + separator + count);
        }

        writer.close();
    }

    @Override
    public void printData() {
        this.printData("0.00");
    }

    @Override
    public void printData(String format) {
        this.printData(format, "0");
    }

    @Override
    public void printData(String xFormat, String yFormat) {
        double minValue = this.data.getStatistics().get(Statistics.MIN);
        double maxValue = this.data.getStatistics().get(Statistics.MAX);
        double barWidth = (maxValue - minValue) / this.numBuckets;

        Histogram1D h1d = new Histogram1D(this.data, Orientation.VERTICAL, this.numBuckets);
        DataSource histogram = new EnumeratedData(h1d, minValue + barWidth / 2, barWidth);

        DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
        DecimalFormat xdf = new DecimalFormat((minValue < 0) ? "+" + xFormat +";-" + xFormat : xFormat, dfs);
        DecimalFormat ydf = new DecimalFormat(yFormat, dfs);

        StringBuilder sb = new StringBuilder();

        sb.append(this.xLabel).append(":");

        sb.append("\nInterval").append(this.blankString(Math.max(0, 2 * xFormat.length() - 4))).append("\tCount");

        for (int r = 0; r < histogram.getRowCount(); r++) {
            Row row = histogram.getRow(r);

            double from = (Double) row.get(0) - barWidth / 2;
            double to = (Double) row.get(0) + barWidth / 2;
            long count = (Long) row.get(1);

            sb.append("\n[").append(xdf.format(from)).append(", ").append(xdf.format(to));

            if (r == histogram.getColumnCount() - 1) {
                sb.append("]");
            } else {
                sb.append(")");
            }

            sb.append("\t").append(ydf.format(count));
        }

        System.out.println("\n" + sb.toString());
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
    public void draw() {
        BarPlot plot = this.getPlot();
        PlotFrame frame = new PlotFrame(plot);
        frame.setVisible(true);
    }

    @Override
    public void exportPlot(String filename) throws IOException {
        BarPlot plot = this.getPlot();
        File f = new File(filename);
        File parent = f.getAbsoluteFile().getParentFile();
        if(!parent.exists() && !parent.mkdirs()) {
            throw new IOException("Unable to create directory "+ parent);
        }
        DrawableWriter writer = DrawableWriterFactory.getInstance().get("image/png");
        writer.write(plot, new FileOutputStream(f), PlotSettings.getWidth(), PlotSettings.getHeight());
    }


    private BarPlot getPlot() {

        // Create histogram plot
        double minValue = this.data.getStatistics().get(Statistics.MIN);
        double maxValue = this.data.getStatistics().get(Statistics.MAX);
        double barWidth = (maxValue - minValue) / numBuckets;

        double displayOffset = 10 + Math.abs(minValue); // Hack to avoid visualization of 0 tick label

        Histogram1D h1d = new Histogram1D(this.data, Orientation.VERTICAL, numBuckets);
        DataSource histogram = new EnumeratedData(h1d, displayOffset + minValue + barWidth / 2, barWidth);

        BarPlot plot = new BarPlot(histogram);

        // Customize plot
        plot.setBackground(PlotSettings.getBackgroundColor());
        plot.setBorderColor(PlotSettings.getBackgroundColor());
        plot.setBarWidth(barWidth);

        plot.setInsets(new Insets2D.Double(
                PlotSettings.getClearInset(),
                PlotSettings.getyAxisInset(),
                PlotSettings.getxAxisInset(),
                PlotSettings.getClearInset()
        ));

        BarPlot.BarRenderer barRenderer = (BarPlot.BarRenderer) plot.getPointRenderers(histogram).get(0);
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
        xAxisRenderer.setIntersection(0);

        Map<Double, String> xTicks = new HashMap<>();
        DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));
        for (int i = 0; i < numBuckets + 1; i++) {
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
