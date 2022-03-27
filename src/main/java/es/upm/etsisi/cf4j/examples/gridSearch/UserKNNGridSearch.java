package es.upm.etsisi.cf4j.examples.gridSearch;

import es.upm.etsisi.cf4j.data.BenchmarkDataModels;
import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.qualityMeasure.recommendation.Precision;
import es.upm.etsisi.cf4j.recommender.knn.UserKNN;
import es.upm.etsisi.cf4j.recommender.knn.userSimilarityMetric.*;
import es.upm.etsisi.cf4j.util.optimization.GridSearch;
import es.upm.etsisi.cf4j.util.optimization.ParamsGrid;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * In this example we tune the parameters of UserKNN recommender using the GridSearch tool. Top 5
 * results with highest Precision score are printed.
 */
public class UserKNNGridSearch {

  public static void main(String[] args) throws IOException {

    DataModel datamodel = BenchmarkDataModels.MovieLens100K();

    ParamsGrid grid = new ParamsGrid();

    grid.addParam("numberOfNeighbors", new int[] {25, 50, 75, 100});
    grid.addParam(
        "metric",
        new UserSimilarityMetric[] {
          new AdjustedCosine(),
          new CJMSD(),
          new Correlation(),
          new Cosine(),
          new Jaccard(),
          new JMSD(),
          new MSD(),
          new PIP(),
          new Singularities(new double[] {3, 4, 5}, new double[] {1, 2}),
          new SpearmanRank()
        });

    grid.addFixedParam("aggregationApproach", UserKNN.AggregationApproach.DEVIATION_FROM_MEAN);

    Map<String, Object> precisionParams = new HashMap<>();
    precisionParams.put("numberOfRecommendations", 5);
    precisionParams.put("relevantThreshold", 4.0);

    GridSearch gs =
        new GridSearch(datamodel, grid, UserKNN.class, Precision.class, precisionParams);
    gs.fit();
    gs.printResults(5, false);
  }
}
