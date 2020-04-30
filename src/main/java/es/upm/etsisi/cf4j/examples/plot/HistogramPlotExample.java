package es.upm.etsisi.cf4j.examples.plot;

import es.upm.etsisi.cf4j.data.BenchmarkDataModels;
import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.Item;
import es.upm.etsisi.cf4j.util.plot.HistogramPlot;

import java.io.IOException;

/**
 * In this example we analyze the average rating of each item that belongs to MovieLens 1M dataset.
 * We show the results using a HistogramPlot.
 */
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
