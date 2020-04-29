package es.upm.etsisi.cf4j.examples.plot;

import es.upm.etsisi.cf4j.data.BenchmarkDataModels;
import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.qualityMeasure.recommendation.Precision;
import es.upm.etsisi.cf4j.qualityMeasure.recommendation.Recall;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.NMF;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.PMF;
import es.upm.etsisi.cf4j.util.plot.XYPlot;

import java.io.IOException;

/**
 * In this example we compare the Precision score (y axis) and the Recall score (x axis) for PMF and
 * NMF recommenders using an XYPlot.
 */
public class XYPlotExample {

  public static void main(String[] args) throws IOException {
    DataModel datamodel = BenchmarkDataModels.MovieLens1M();

    int[] numberOfRecommendations = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

    String[] labels = new String[numberOfRecommendations.length];
    for (int i = 0; i < labels.length; i++) {
      labels[i] = String.valueOf(numberOfRecommendations[i]);
    }

    XYPlot plot = new XYPlot(labels, "Recall", "Precision");

    PMF pmf = new PMF(datamodel, 10, 50, 43);
    pmf.fit();
    plot.addSeries("PMF");
    plot.setLabelsVisible("PMF");

    for (int N : numberOfRecommendations) {
      Precision precision = new Precision(pmf, N, 4.0);
      double precisionScore = precision.getScore();

      Recall recall = new Recall(pmf, N, 4.0);
      double recallScore = recall.getScore();

      plot.setXY("PMF", String.valueOf(N), precisionScore, recallScore);
    }

    NMF nmf = new NMF(datamodel, 10, 50, 43);
    nmf.fit();
    plot.addSeries("NMF");
    plot.setLabelsVisible("NMF");

    for (int N : numberOfRecommendations) {
      Precision precision = new Precision(nmf, N, 4.0);
      double precisionScore = precision.getScore();

      Recall recall = new Recall(nmf, N, 4.0);
      double recallScore = recall.getScore();

      plot.setXY("NMF", String.valueOf(N), precisionScore, recallScore);
    }

    plot.draw();
    plot.exportPlot("exports/xy-plot.png");
    plot.printData();
    plot.exportData("exports/xy-plot-data.csv");
  }
}
