package es.upm.etsisi.cf4j.examples.recommender;

import es.upm.etsisi.cf4j.data.BenchmarkDataModels;
import es.upm.etsisi.cf4j.util.plot.LinePlot;
import es.upm.etsisi.cf4j.util.Maths;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.qualityMeasure.prediction.RMSE;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.*;

import java.io.IOException;

/**
 * In this example we compare the RMSE score for different matrix factorization models varying the
 * number of latent factors.
 */
public class MatrixFactorizationComparison {

  private static final int[] NUM_FACTORS = Maths.range(5, 25, 5);

  private static final int NUM_ITERS = 50;

  private static final long RANDOM_SEED = 43;

  public static void main(String[] args) throws IOException {

    // DataModel load
    DataModel datamodel = BenchmarkDataModels.MovieLens100K();

    // To store results
    LinePlot plot = new LinePlot(NUM_FACTORS, "Number of latent factors", "RMSE");

    // Evaluate PMF Recommender
    plot.addSeries("PMF");
    for (int factors : NUM_FACTORS) {
      Recommender pmf = new PMF(datamodel, factors, NUM_ITERS, RANDOM_SEED);
      pmf.fit();

      QualityMeasure rmse = new RMSE(pmf);
      double rmseScore = rmse.getScore();
      plot.setValue("PMF", factors, rmseScore);
    }

    // Evaluate BNMF Recommender
    plot.addSeries("BNMF");
    for (int factors : NUM_FACTORS) {
      Recommender bnmf = new BNMF(datamodel, factors, NUM_ITERS, 0.2, 10, RANDOM_SEED);
      bnmf.fit();

      QualityMeasure rmse = new RMSE(bnmf);
      double rmseScore = rmse.getScore();
      plot.setValue("BNMF", factors, rmseScore);
    }

    // Evaluate BiasedMF Recommender
    plot.addSeries("BiasedMF");
    for (int factors : NUM_FACTORS) {
      Recommender biasedmf = new BiasedMF(datamodel, factors, NUM_ITERS, RANDOM_SEED);
      biasedmf.fit();

      QualityMeasure rmse = new RMSE(biasedmf);
      double rmseScore = rmse.getScore();
      plot.setValue("BiasedMF", factors, rmseScore);
    }

    // Evaluate NMF Recommender
    plot.addSeries("NMF");
    for (int factors : NUM_FACTORS) {
      Recommender nmf = new NMF(datamodel, factors, NUM_ITERS, RANDOM_SEED);
      nmf.fit();

      QualityMeasure rmse = new RMSE(nmf);
      double rmseScore = rmse.getScore();
      plot.setValue("NMF", factors, rmseScore);
    }

    // Evaluate CLiMF Recommender
    plot.addSeries("CLiMF");
    for (int factors : NUM_FACTORS) {
      Recommender climf = new CLiMF(datamodel, factors, NUM_ITERS, RANDOM_SEED);
      climf.fit();

      QualityMeasure rmse = new RMSE(climf);
      double rmseScore = rmse.getScore();
      plot.setValue("CLiMF", factors, rmseScore);
    }

    // Evaluate SVDPlusPlus Recommender
    plot.addSeries("SVDPlusPlus");
    for (int factors : NUM_FACTORS) {
      Recommender svdPlusPlus = new SVDPlusPlus(datamodel, factors, NUM_ITERS, RANDOM_SEED);
      svdPlusPlus.fit();

      QualityMeasure rmse = new RMSE(svdPlusPlus);
      double rmseScore = rmse.getScore();
      plot.setValue("SVDPlusPlus", factors, rmseScore);
    }

    // Evaluate HPF Recommender
    plot.addSeries("HPF");
    for (int factors : NUM_FACTORS) {
      Recommender hpf = new HPF(datamodel, factors, NUM_ITERS, RANDOM_SEED);
      hpf.fit();

      QualityMeasure rmse = new RMSE(hpf);
      double rmseScore = rmse.getScore();
      plot.setValue("HPF", factors, rmseScore);
    }

    // Evaluate URP Recommender
    plot.addSeries("URP");
    for (int factors : NUM_FACTORS) {
      Recommender urp =
          new URP(
              datamodel, factors, new double[] {1.0, 2.0, 3.0, 4.0, 5.0}, NUM_ITERS, RANDOM_SEED);
      urp.fit();

      QualityMeasure rmse = new RMSE(urp);
      double rmseScore = rmse.getScore();
      plot.setValue("URP", factors, rmseScore);
    }

    // Print results
    plot.printData("0", "0.0000");
  }
}
