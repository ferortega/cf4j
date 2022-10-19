package es.upm.etsisi.cf4j.examples.recommender;

import es.upm.etsisi.cf4j.data.BenchmarkDataModels;
import es.upm.etsisi.cf4j.util.Maths;
import es.upm.etsisi.cf4j.util.plot.LinePlot;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.qualityMeasure.recommendation.Precision;
import es.upm.etsisi.cf4j.qualityMeasure.recommendation.Recall;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.recommender.knn.UserKNN;
import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.qualityMeasure.prediction.Coverage;
import es.upm.etsisi.cf4j.qualityMeasure.prediction.MAE;
import es.upm.etsisi.cf4j.recommender.knn.userSimilarityMetric.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * In this example we compare the MAE, Coverage, Precision and Recall quality measures scores for
 * different similarity metrics applied to user-to-user knn based collaborative filtering. Each
 * similarity metric is tested with different number of neighbors.
 */
public class UserKnnComparison {

  private static final int[] numNeighbors = Maths.range(100, 300, 50);

  private static final UserKNN.AggregationApproach AGGREGATION_APPROACH =
      UserKNN.AggregationApproach.DEVIATION_FROM_MEAN;

  public static void main(String[] args) throws IOException {

    // DataModel load
    DataModel datamodel = BenchmarkDataModels.MovieLens100K();

    // To store results
    LinePlot maePlot = new LinePlot(numNeighbors, "Number of neighbors", "MAE");
    LinePlot coveragePlot = new LinePlot(numNeighbors, "Number of neighbors", "Coverage");
    LinePlot precisionPlot = new LinePlot(numNeighbors, "Number of neighbors", "Precision");
    LinePlot recallPlot = new LinePlot(numNeighbors, "Number of neighbors", "Recall");

    // Create similarity metrics
    List<UserSimilarityMetric> metrics = new ArrayList<>();
    metrics.add(new AdjustedCosine());
    metrics.add(new CJMSD());
    metrics.add(new Correlation());
    metrics.add(new Cosine());
    metrics.add(new Jaccard());
    metrics.add(new JMSD());
    metrics.add(new MSD());
    metrics.add(new PIP());
    metrics.add(new Singularities(new double[] {3, 4, 5}, new double[] {1, 2}));
    metrics.add(new SpearmanRank());

    // Evaluate UserKNN recommender
    for (UserSimilarityMetric metric : metrics) {
      String metricName = metric.getClass().getSimpleName();

      maePlot.addSeries(metricName);
      coveragePlot.addSeries(metricName);
      precisionPlot.addSeries(metricName);
      recallPlot.addSeries(metricName);

      for (int k : numNeighbors) {
        Recommender knn = new UserKNN(datamodel, k, metric, AGGREGATION_APPROACH);
        knn.fit();

        QualityMeasure mae = new MAE(knn);
        double maeScore = mae.getScore();
        maePlot.setValue(metricName, k, maeScore);

        QualityMeasure coverage = new Coverage(knn);
        double coverageScore = mae.getScore();
        coveragePlot.setValue(metricName, k, coverageScore);

        QualityMeasure precision = new Precision(knn, 10, 4);
        double precisionScore = mae.getScore();
        precisionPlot.setValue(metricName, k, precisionScore);

        QualityMeasure recall = new Recall(knn, 10, 4);
        double recallScore = mae.getScore();
        recallPlot.setValue(metricName, k, recallScore);
      }
    }

    // Print results
    maePlot.printData("0", "0.0000");
    coveragePlot.printData("0", "0.0000");
    precisionPlot.printData("0", "0.0000");
    recallPlot.printData("0", "0.0000");
  }
}
