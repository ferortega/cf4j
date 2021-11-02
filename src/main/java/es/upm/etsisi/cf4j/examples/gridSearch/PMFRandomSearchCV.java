package es.upm.etsisi.cf4j.examples.gridSearch;

import es.upm.etsisi.cf4j.data.BenchmarkDataModels;
import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.qualityMeasure.prediction.MAE;
import es.upm.etsisi.cf4j.qualityMeasure.prediction.MSE;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.BiasedMF;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.PMF;
import es.upm.etsisi.cf4j.util.optimization.GridSearch;
import es.upm.etsisi.cf4j.util.optimization.GridSearchCV;
import es.upm.etsisi.cf4j.util.optimization.ParamsGrid;
import es.upm.etsisi.cf4j.util.optimization.RandomSearchCV;

import java.io.IOException;

/**
 * In this example we tune the hyper-parameters of PMF recommender using the RandomSearchCV tool.
 * Top 10 results with lowest Mean Squared Error (MSE) are printed.
 */
public class PMFRandomSearchCV {

  public static void main(String[] args) throws IOException {

    DataModel ml100k = BenchmarkDataModels.MovieLens100K();

    ParamsGrid paramsGrid = new ParamsGrid();

    paramsGrid.addParam("numFactors", new int[]{2,4, 6, 8, 10});
    paramsGrid.addParam("lambda", new double[]{0.001, 0.01, 0.1, 1.0, 10.0});
    paramsGrid.addParam("gamma", new double[]{0.001, 0.01, 0.1, 1.0, 10.0});
    paramsGrid.addParam("numIters", new int[]{25, 50, 75, 100});

    paramsGrid.addFixedParam("seed", 42L);

    RandomSearchCV randomSearchCV = new RandomSearchCV(ml100k, paramsGrid, PMF.class, MSE.class, 5, 0.25);
    randomSearchCV.fit();

    randomSearchCV.printResults(10);
  }
}
