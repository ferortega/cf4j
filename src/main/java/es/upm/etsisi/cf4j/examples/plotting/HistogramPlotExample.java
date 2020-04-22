package es.upm.etsisi.cf4j.examples.plotting;

import es.upm.etsisi.cf4j.data.BenchmarkDataModels;
import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.Item;
import es.upm.etsisi.cf4j.plot.HistogramPlot;

import java.io.IOException;

public class HistogramPlotExample {

    public static void main(String[] args) throws IOException {
        DataModel datamodel = BenchmarkDataModels.MovieLens1M();

        HistogramPlot plot = new HistogramPlot("Item rating average", 10);

        for (Item item : datamodel.getItems()) {
            plot.addValue(item.getRatingAverage());
        }

        plot.draw();
        plot.exportPlot("exports/histogram-plot.png");
        plot.printData();
        plot.exportData("exports/histogram-plot-data.csv");
    }
}
