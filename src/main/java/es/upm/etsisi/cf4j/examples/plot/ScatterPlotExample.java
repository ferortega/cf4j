package es.upm.etsisi.cf4j.examples.plot;

import es.upm.etsisi.cf4j.data.BenchmarkDataModels;
import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.util.plot.ScatterPlot;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.BiasedMF;

import java.io.IOException;

/**
 * In this example we build an ScatterPlot comparing the number of ratings of each test user with
 * his/her averaged prediction error using BiasedMF as recommender.
 */
public class ScatterPlotExample {

  public static void main(String[] args) throws IOException {
    DataModel datamodel = BenchmarkDataModels.MovieLens1M();

    BiasedMF biasedMF = new BiasedMF(datamodel, 10, 50, 43);
    biasedMF.fit();

    ScatterPlot plot = new ScatterPlot("Number of ratings", "Averaged user prediction error");

    for (TestUser testUser : datamodel.getTestUsers()) {
      double[] predictions = biasedMF.predict(testUser);

      double sum = 0;

      for (int pos = 0; pos < testUser.getNumberOfTestRatings(); pos++) {
        double rating = testUser.getTestRatingAt(pos);
        double prediction = predictions[pos];
        sum += Math.pow(rating - prediction, 2);
      }

      double userError = sum / testUser.getNumberOfTestRatings();

      plot.addPoint(testUser.getNumberOfRatings(), userError);
    }

    plot.draw();
    plot.exportData("exports/scatter-plot-data.csv");
    plot.printData("0", "0.00");
    plot.exportPlot("exports/scatter-plot.png");
  }
}
