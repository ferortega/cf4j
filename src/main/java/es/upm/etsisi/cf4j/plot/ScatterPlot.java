package es.upm.etsisi.cf4j.plot;

import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.graphics.Insets2D;
import de.erichseifert.gral.graphics.Label;
import de.erichseifert.gral.io.plots.DrawableWriter;
import de.erichseifert.gral.io.plots.DrawableWriterFactory;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.points.DefaultPointRenderer2D;
import de.erichseifert.gral.plots.points.PointRenderer;
import org.apache.commons.math3.util.Pair;

import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
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
    public void exportData(String filename, String separator) throws IOException {
        File f = new File(filename);
        File parent = f.getAbsoluteFile().getParentFile();
        if(!parent.exists() && !parent.mkdirs()) {
            throw new IOException("Unable to create directory "+ parent);
        }

        PrintWriter writer = new PrintWriter(f);

        writer.print("\"" + this.xLabel + "\"" + separator + "\"" + this.yLabel + "\"");

        for (Pair<Double, Double> point : this.points) {
            writer.print("\n" + point.getFirst() + separator + point.getSecond());
        }

        writer.close();
    }

    @Override
    public void printData(String xFormat, String yFormat) {
        DecimalFormat xdf = new DecimalFormat(xFormat);
        DecimalFormat ydf = new DecimalFormat(yFormat);

        StringBuilder sb = new StringBuilder();

        sb.append(this.xLabel).append(this.blankString(Math.max(0, xFormat.length() - this.xLabel.length())));
        sb.append("  ");
        sb.append(this.yLabel).append(this.blankString(Math.max(0, yFormat.length() - this.yLabel.length())));

        for (Pair<Double, Double> point : this.points) {
            sb.append("\n");
            sb.append(xdf.format(point.getFirst())).append(this.blankString(Math.max(0, xFormat.length() - this.xLabel.length())));
            sb.append("  ");
            sb.append(ydf.format(point.getSecond())).append(this.blankString(Math.max(0, yFormat.length() - this.yLabel.length())));
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

        DataTable data = new DataTable(Double.class, Double.class);
        for (Pair<Double, Double> point : this.points) {
            data.add(point.getFirst(), point.getSecond());
        }

        DataSeries series = new DataSeries("Series", data);

        XYPlot plot = new XYPlot(series);

        plot.setBackground(PlotSettings.getBackgroundColor());

        PointRenderer pr = new DefaultPointRenderer2D();
        pr.setShape(new Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0));
        pr.setColor(PlotSettings.getColor(0));
        plot.setPointRenderers(series, pr);

        plot.setInsets(new Insets2D.Double(
                PlotSettings.getClearInset(),
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

        plot.getNavigator().setZoom(0.9);
        plot.getNavigator().setZoomable(false);

        return plot;
    }
}
