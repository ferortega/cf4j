package es.upm.etsisi.cf4j;

import es.upm.etsisi.cf4j.data.BenchmarkDataModels;
import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.metrics.prediction.MeanAbsoluteError;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.PMF;

public class TestWIP {

    public static void main (String[] args) throws Exception {
        DataModel ml100k = BenchmarkDataModels.MovieLens100K();

        PMF pmf = new PMF(ml100k, 10, 25);
        pmf.fit();

        MeanAbsoluteError mae = new MeanAbsoluteError(pmf);
        mae.fit();

        System.out.println(mae.getScore());
        System.out.println("mean=" + mae.getScore() + "; sd=" + mae.getScoreStandardDeviation() + "; margin ci95=" + mae.get95ConfidenceIntervalMargin() + "; margin ci99=" + mae.get99ConfidenceIntervalMargin());
    }
}
