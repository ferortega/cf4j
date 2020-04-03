package es.upm.etsisi.cf4j.examples;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.DataSet;
import es.upm.etsisi.cf4j.data.RandomSplitDataSet;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.qualityMeasure.prediction.MSE;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.NMF;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.PMF;

import java.io.IOException;

/**
 * In this example we visualize MSE quality measure with two different recommenders: PMF and NMF.
 * Example used in the Getting Started section of the readme.md in github repository
 * @author Fernando Ortega
 */
public class GettingStartedExample {
    public static void main (String [] args) throws IOException {

        // Step 0: Define parameters
        String filename = "src/main/resources/datasets/ml100k.data";
        double testUsers = 0.2;
        double testItems = 0.2;
        String separator = "\t";
        long seed = 43;

        // Step 1: Preparing the dataset to be splitted in two parts: training and test (Load MovieLens 100K dataset).
        DataSet ml100k = new RandomSplitDataSet(filename, testUsers, testItems, separator, seed);

        // Step 2: Storing the data in the DataModel to be efficiently accessed by the recommenders.
        DataModel datamodel = new DataModel(ml100k);

        // Step 3.a: Generating an specific recommender (Probabilistic matrix factorization).
        PMF pmf = new PMF(datamodel, 10, 100, 0.1, 0.01, 43);
        pmf.fit();

        // Step 3.b: Generating an specific recommender (Non-negative Matrix Factorization).
        NMF nmf = new NMF(datamodel, 10, 100, 43);
        nmf.fit();

        // Step 4: Evaluate the recommenders
        QualityMeasure mse;

        // Step 4.a: Setting up a MAE quality measure with PMF recommender.
        mse = new MSE(pmf);

        // Step 5.a: Printing the results
        System.out.println("\nMSE (PMF): " + mse.getScore());

        // Step 4.b: Setting up a MAE quality measure with NMF recommender.
        mse = new MSE(nmf);

        // Step 5.b: Printing the results
        System.out.println("MSE (NMF): " + mse.getScore());
    }
}
