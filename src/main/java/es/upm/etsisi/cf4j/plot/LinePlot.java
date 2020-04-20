package es.upm.etsisi.cf4j.plot;

import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.graphics.Insets2D;
import de.erichseifert.gral.graphics.Label;
import de.erichseifert.gral.graphics.Location;
import de.erichseifert.gral.graphics.Orientation;
import de.erichseifert.gral.io.plots.DrawableWriter;
import de.erichseifert.gral.io.plots.DrawableWriterFactory;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import org.apache.commons.math3.util.Pair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

public class LinePlot extends Plot {

    private List<Pair<String, double[]>> series;

    private double[] xs;

    private String xLabel;

    private String yLabel;

    private boolean hideLengend;


    public LinePlot(int[] xs, String xLabel, String yLabel) {
        this(Arrays.stream(xs).asDoubleStream().toArray(), xLabel, yLabel, false);
    }

    public LinePlot(int[] xs, String xLabel, String yLabel, boolean hideLengend) {
        this(Arrays.stream(xs).asDoubleStream().toArray(), xLabel, yLabel, hideLengend);
    }

    public LinePlot(double[] xs, String xLabel, String yLabel) {
        this(xs, xLabel, yLabel, false);
    }

    public LinePlot(double[] xs, String xLabel, String yLabel, boolean hideLengend) {
        this.xs = xs;
        this.xLabel = xLabel;
        this.yLabel = yLabel;
        this.hideLengend = hideLengend;

        this.series = new ArrayList<>();
    }

    public void addSerie(String seriesName, double[] y) {
        this.series.add(new Pair(seriesName, y));
    }

    public void addSerie(String seriesName) {
        this.series.add(new Pair(seriesName, new double[this.xs.length]));
    }

    public void setValue(String seriesName, int x, double y) {
        this.setValue(seriesName, (double) x, y);
    }

    public void setValue(String seriesName, double x, double y) {
        int xIndex = 0;
        while (this.xs[xIndex] != x) {
            xIndex++;
        }

        int seriesIndex = 0;
        while (!this.series.get(seriesIndex).getKey().equals(seriesName)) {
            seriesIndex++;
        }

        this.series.get(seriesIndex).getValue()[xIndex] = y;
    }

    @Override
    public void exportData(String filename, String separator) throws IOException {
        File f = new File(filename);
        File parent = f.getAbsoluteFile().getParentFile();
        if(!parent.exists() && !parent.mkdirs()) {
            throw new IOException("Unable to create directory "+ parent);
        }

        PrintWriter writer = new PrintWriter(f);

        writer.print("\"" + this.xLabel + "\"");

        for (Pair<String, double[]> s : this.series) {
            String seriesName = s.getKey();
            writer.print(separator + "\"" + seriesName + "\"");
        }

        for (int row = 0; row < this.xs.length; row++) {
            writer.print("\n" + this.xs[row]);
            for (Pair<String, double[]> s : this.series) {
                double[] ys = s.getValue();
                writer.print(separator + ys[row]);
            }
        }

        writer.close();
    }

    @Override
    public void printData(String xFormat, String yFormat) {
        DecimalFormat xdf = new DecimalFormat(xFormat);
        DecimalFormat ydf = new DecimalFormat(yFormat);

        StringBuilder sb = new StringBuilder();

        sb.append(this.yLabel).append(":\n");

        sb.append(this.xLabel).append(this.blankString(Math.max(0, xFormat.length() - this.xLabel.length())));

        for (Pair<String, double[]> s : this.series) {
            String seriesName = s.getKey();
            int blackLength = Math.max(0, yFormat.length() - seriesName.length());
            sb.append("  ").append(seriesName).append(this.blankString(blackLength));
        }

        for (int row = 0; row < this.xs.length; row++) {
            int blackLength = Math.max(0, this.xLabel.length() - xFormat.length());
            sb.append("\n").append(xdf.format(this.xs[row])).append(this.blankString(blackLength));

            for (Pair<String, double[]> s : this.series) {
                String seriesName = s.getKey();
                double[] ys = s.getValue();
                blackLength = Math.max(0, seriesName.length() - yFormat.length());
                sb.append("  ").append(ydf.format(ys[row])).append(this.blankString(blackLength));
            }
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
        XYPlot plot = this.getPlot();
        PlotFrame frame = new PlotFrame(plot);
        frame.setVisible(true);
    }

    @Override
    public void exportPlot(String filename) throws IOException {
        XYPlot plot = this.getPlot();
        File f = new File(filename);
        File parent = f.getAbsoluteFile().getParentFile();
        if(!parent.exists() && !parent.mkdirs()) {
            throw new IOException("Unable to create directory "+ parent);
        }
        DrawableWriter writer = DrawableWriterFactory.getInstance().get("image/png");
        writer.write(plot, new FileOutputStream(f), PlotSettings.getWidth(), PlotSettings.getHeight());
    }


    private XYPlot getPlot() {

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

        plot.setBackground(PlotSettings.getBackgroundColor());

        plot.setInsets(new Insets2D.Double(
                (this.hideLengend) ? PlotSettings.getClearInset() : PlotSettings.getLegendInset(),
                PlotSettings.getyAxisInset(),
                PlotSettings.getxAxisInset(),
                PlotSettings.getClearInset()
        ));

        plot.getAxisRenderer(XYPlot.AXIS_X).setLabel(new Label(xLabel));
        plot.getAxisRenderer(XYPlot.AXIS_X).getLabel().setFont(PlotSettings.getPrimaryFont());
        plot.getAxisRenderer(XYPlot.AXIS_X).setLabelDistance(PlotSettings.getxAxisLabelDistance());

        plot.getAxisRenderer(XYPlot.AXIS_X).setTickFont(PlotSettings.getSecondaryFont());
        plot.getAxisRenderer(XYPlot.AXIS_X).setTickLabelFormat(NumberFormat.getInstance(Locale.US));
        plot.getAxisRenderer(XYPlot.AXIS_X).setTicksAutoSpaced(true);

        plot.getAxisRenderer(XYPlot.AXIS_Y).setLabel(new Label(yLabel));
        plot.getAxisRenderer(XYPlot.AXIS_Y).getLabel().setFont(PlotSettings.getPrimaryFont());
        plot.getAxisRenderer(XYPlot.AXIS_Y).getLabel().setRotation(90);
        plot.getAxisRenderer(XYPlot.AXIS_Y).setLabelDistance(PlotSettings.getyAxisLabelDistance());

        plot.getAxisRenderer(XYPlot.AXIS_Y).setTickFont(PlotSettings.getSecondaryFont());
        plot.getAxisRenderer(XYPlot.AXIS_Y).setTickLabelFormat(NumberFormat.getInstance(Locale.US));
        plot.getAxisRenderer(XYPlot.AXIS_Y).setTicksAutoSpaced(true);

        plot.getAxisRenderer(XYPlot.AXIS_X).setIntersection(-Double.MAX_VALUE);
        plot.getAxisRenderer(XYPlot.AXIS_Y).setIntersection(-Double.MAX_VALUE);

        if (!this.hideLengend) {
            plot.setLegendLocation(Location.NORTH);
            plot.setLegendVisible(true);
            plot.setLegendDistance(PlotSettings.getLegendDistance());
            plot.getLegend().setBorderStroke(null);
            plot.getLegend().setOrientation(Orientation.HORIZONTAL);
            plot.getLegend().setAlignmentX(0.5);
            plot.getLegend().setFont(PlotSettings.getPrimaryFont());

            plot.getNavigator().setZoom(0.9);
            plot.getNavigator().setZoomable(false);
        }

        return plot;
    }
}
