package es.upm.etsisi.cf4j.examples;

import es.upm.etsisi.cf4j.data.BenchmarkDataModels;
import es.upm.etsisi.cf4j.util.plot.LinePlot;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.qualityMeasure.prediction.MSE;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.PMF;
import es.upm.etsisi.cf4j.data.DataModel;

import java.io.IOException;

/**
 * In this example we analyze how the Mean Squared Error (MSE) varies according to the value of the
 * regularization term in Probabilistic Matrix Factorization (PMF). This is the example included in
 * the Getting Started section of the readme.md.
 */
public class GettingStartedExample {
  public static void main(String[] args) throws IOException {

    DataModel datamodel = BenchmarkDataModels.MovieLens100K();

    double[] regValues = {
      0.000, 0.025, 0.05, 0.075, 0.100, 0.125, 0.150, 0.175, 0.200, 0.225, 0.250
    };
    LinePlot plot = new LinePlot(regValues, "regularization", "MSE");

    plot.addSeries("PMF");

    for (double reg : regValues) {
      PMF pmf = new PMF(datamodel, 6, 50, reg, 0.01, 43);
      pmf.fit();

      QualityMeasure mse = new MSE(pmf);
      double mseScore = mse.getScore();

      plot.setValue("PMF", reg, mseScore);
    }

    plot.draw();
    plot.printData("0.000");
  }
}
