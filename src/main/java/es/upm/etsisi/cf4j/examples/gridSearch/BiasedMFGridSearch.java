package es.upm.etsisi.cf4j.examples.gridSearch;

import es.upm.etsisi.cf4j.data.BenchmarkDataModels;
import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.qualityMeasure.prediction.MAE;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.BiasedMF;
import es.upm.etsisi.cf4j.util.optimization.GridSearch;
import es.upm.etsisi.cf4j.util.optimization.ParamsGrid;

import java.io.IOException;

/**
 * In this example we tune the hyper-parameters of BiasedMF recommender using the GridSearch tool.
 * Top 5 results with lowest Mean Absolute Error (MAE) are printed.
 */
public class BiasedMFGridSearch {

  public static void main(String[] args) throws IOException {

    DataModel datamodel = BenchmarkDataModels.MovieLens100K();

    ParamsGrid grid = new ParamsGrid();

    grid.addParam("numIters", new int[] {50, 75, 100});
    grid.addParam("numFactors", new int[] {5, 10, 15});
    grid.addParam("lambda", new double[] {0.05, 0.10, 0.15});
    grid.addParam("gamma", new double[] {0.001, 0.01, 0.1});

    grid.addFixedParam("seed", 43L);

    GridSearch gs = new GridSearch(datamodel, grid, BiasedMF.class, MAE.class);
    gs.fit();
    gs.printResults(5);
  }
}
