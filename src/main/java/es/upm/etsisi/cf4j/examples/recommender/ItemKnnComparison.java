package es.upm.etsisi.cf4j.examples.recommender;

import es.upm.etsisi.cf4j.data.BenchmarkDataModels;
import es.upm.etsisi.cf4j.util.Maths;
import es.upm.etsisi.cf4j.util.plot.LinePlot;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.qualityMeasure.recommendation.NDCG;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.recommender.knn.ItemKNN;
import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.qualityMeasure.prediction.MSLE;
import es.upm.etsisi.cf4j.recommender.knn.itemSimilarityMetric.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * In this example we compare the MSLE and nDCG quality measures scores for different similarity
 * metrics applied to item-to-item knn based collaborative filtering. Each similarity metric is
 * tested with different number of neighbors.
 */
public class ItemKnnComparison {

  private static final int[] NUM_NEIGHBORS = Maths.range(100, 300, 50);

  private static final ItemKNN.AggregationApproach AGGREGATION_APPROACH =
      ItemKNN.AggregationApproach.MEAN;

  public static void main(String[] args) throws IOException {

    // DataModel load
    DataModel datamodel = BenchmarkDataModels.MovieLens100K();

    // To store results
    LinePlot mslePlot = new LinePlot(NUM_NEIGHBORS, "Number of neighbors", "MSLE");
    LinePlot ndcgPlot = new LinePlot(NUM_NEIGHBORS, "Number of neighbors", "nDCG");

    // Create similarity metrics
    ArrayList<ItemSimilarityMetric> metrics = new ArrayList<>();
    metrics.add(new AdjustedCosine());
    metrics.add(new Correlation());
    metrics.add(new Cosine());
    metrics.add(new Jaccard());
    metrics.add(new JMSD());
    metrics.add(new MSD());
    metrics.add(new PIP());
    metrics.add(new Singularities(new double[] {3, 4, 5}, new double[] {1, 2}));
    metrics.add(new SpearmanRank());

    // Evaluate ItemKNN recommender for each similarity metric
    for (ItemSimilarityMetric metric : metrics) {
      String metricName = metric.getClass().getSimpleName();

      mslePlot.addSeries(metricName);
      ndcgPlot.addSeries(metricName);

      for (int k : NUM_NEIGHBORS) {
        Recommender knn = new ItemKNN(datamodel, k, metric, AGGREGATION_APPROACH);
        knn.fit();

        QualityMeasure msle = new MSLE(knn);
        double msleScore = msle.getScore();
        mslePlot.setValue(metricName, k, msleScore);

        QualityMeasure ndcg = new NDCG(knn, 10);
        double ndcgScore = ndcg.getScore();
        ndcgPlot.setValue(metricName, k, ndcgScore);
      }
    }

    // Print results
    mslePlot.printData("0", "0.0000");
    ndcgPlot.printData("0", "0.0000");
  }
}
