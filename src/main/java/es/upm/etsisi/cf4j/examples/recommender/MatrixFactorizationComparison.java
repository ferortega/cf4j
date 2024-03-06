package es.upm.etsisi.cf4j.examples.recommender;

import es.upm.etsisi.cf4j.data.BenchmarkDataModels;
import es.upm.etsisi.cf4j.scorer.Scorer;
import es.upm.etsisi.cf4j.scorer.prediction.RootMeanSquaredError;
import es.upm.etsisi.cf4j.util.plot.LinePlot;
import es.upm.etsisi.cf4j.util.Maths;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.data.DataModel;
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

      Scorer rmse = new RootMeanSquaredError(pmf);
      rmse.fit();
      plot.setValue("PMF", factors, rmse.getScore());
    }

    // Evaluate BNMF Recommender
    plot.addSeries("BNMF");
    for (int factors : NUM_FACTORS) {
      Recommender bnmf = new BNMF(datamodel, factors, NUM_ITERS, 0.2, 10, RANDOM_SEED);
      bnmf.fit();

      Scorer rmse = new RootMeanSquaredError(bnmf);
      rmse.fit();
      plot.setValue("BNMF", factors, rmse.getScore());
    }

    // Evaluate BiasedMF Recommender
    plot.addSeries("BiasedMF");
    for (int factors : NUM_FACTORS) {
      Recommender biasedmf = new BiasedMF(datamodel, factors, NUM_ITERS, RANDOM_SEED);
      biasedmf.fit();

      Scorer rmse = new RootMeanSquaredError(biasedmf);
      rmse.fit();
      plot.setValue("BiasedMF", factors, rmse.getScore());
    }

    // Evaluate NMF Recommender
    plot.addSeries("NMF");
    for (int factors : NUM_FACTORS) {
      Recommender nmf = new NMF(datamodel, factors, NUM_ITERS, RANDOM_SEED);
      nmf.fit();

      Scorer rmse = new RootMeanSquaredError(nmf);
      rmse.fit();
      plot.setValue("NMF", factors, rmse.getScore());
    }

    // Evaluate CLiMF Recommender
    plot.addSeries("CLiMF");
    for (int factors : NUM_FACTORS) {
      Recommender climf = new CLiMF(datamodel, factors, NUM_ITERS, RANDOM_SEED);
      climf.fit();

      Scorer rmse = new RootMeanSquaredError(climf);
      rmse.fit();
      plot.setValue("CLiMF", factors, rmse.getScore());
    }

    // Evaluate SVDPlusPlus Recommender
    plot.addSeries("SVDPlusPlus");
    for (int factors : NUM_FACTORS) {
      Recommender svdPlusPlus = new SVDPlusPlus(datamodel, factors, NUM_ITERS, RANDOM_SEED);
      svdPlusPlus.fit();

      Scorer rmse = new RootMeanSquaredError(svdPlusPlus);
      rmse.fit();
      plot.setValue("SVDPlusPlus", factors, rmse.getScore());
    }

    // Evaluate HPF Recommender
    plot.addSeries("HPF");
    for (int factors : NUM_FACTORS) {
      Recommender hpf = new HPF(datamodel, factors, NUM_ITERS, RANDOM_SEED);
      hpf.fit();

      Scorer rmse = new RootMeanSquaredError(hpf);
      rmse.fit();
      plot.setValue("HPF", factors, rmse.getScore());
    }

    // Evaluate URP Recommender
    plot.addSeries("URP");
    for (int factors : NUM_FACTORS) {
      Recommender urp =
          new URP(
              datamodel, factors, new double[] {1.0, 2.0, 3.0, 4.0, 5.0}, NUM_ITERS, RANDOM_SEED);
      urp.fit();

      Scorer rmse = new RootMeanSquaredError(urp);
      rmse.fit();
      plot.setValue("URP", factors, rmse.getScore());
    }

    // Print results
    plot.printData("0", "0.0000");
  }
}
