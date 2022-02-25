package es.upm.etsisi.cf4j.examples.recommender;

import es.upm.etsisi.cf4j.data.BenchmarkDataModels;
import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.qualityMeasure.prediction.MAE;
import es.upm.etsisi.cf4j.qualityMeasure.prediction.MSE;
import es.upm.etsisi.cf4j.qualityMeasure.prediction.RMSE;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.recommender.neural.GMF;
import es.upm.etsisi.cf4j.util.Range;
import es.upm.etsisi.cf4j.util.plot.LinePlot;

import java.io.IOException;

public class GMFComparison {

    private static final int[] NUM_FACTORS = Range.ofIntegers(5, 5, 6);

    private static final int[] NUM_ITERS = Range.ofIntegers(50, 100, 9);

    private static final double[] LEARNING_RATE = new double[]{1.0, 0.1, 0.01, 0.001};

    private static final long RANDOM_SEED = 43;

    public static void main(String[] args) throws IOException {

        // DataModel load
        DataModel datamodel = BenchmarkDataModels.MovieLens100K();

        // To store results
        int[] idTest = new int[NUM_FACTORS.length*NUM_ITERS.length*LEARNING_RATE.length];
        for(int i = 0;i<idTest.length;i++) idTest[i] = i+1;
        LinePlot plot = new LinePlot(idTest, "Test","", true);

        plot.addSeries("Number of factors");
        plot.addSeries("Number of iterations");
        plot.addSeries("Learning rate");
        plot.addSeries("MAE");
        plot.addSeries("MSE");
        plot.addSeries("RMSE");

        int i =1;
        for (int factors : NUM_FACTORS) {
            for (int iters : NUM_ITERS) {
                for (double lr : LEARNING_RATE) {
                    Recommender gmf = new GMF(datamodel, factors, iters, lr, RANDOM_SEED);
                    gmf.fit();

                    QualityMeasure mae = new MAE(gmf);
                    double maeScore = mae.getScore();

                    QualityMeasure mse = new MSE(gmf);
                    double mseScore = mse.getScore();

                    QualityMeasure rmse = new RMSE(gmf);
                    double rmseScore = rmse.getScore();

                    plot.setValue("Number of factors", i, factors);
                    plot.setValue("Number of iterations", i, iters);
                    plot.setValue("Learning rate", i, lr);
                    plot.setValue("MAE", i, maeScore);
                    plot.setValue("MSE", i, mseScore);
                    plot.setValue("RMSE", i, rmseScore);
                    i++;
                }
            }
        }

        // Print results
        plot.printData("0", "0.0000");
    }
}
