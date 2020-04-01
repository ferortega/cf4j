package es.upm.etsisi.cf4j.examples;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.DataSet;
import es.upm.etsisi.cf4j.data.RandomSplitDataSet;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.qualityMeasure.prediction.MSE;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.NMF;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.PMF;

/**
 * Example used in the Getting Started section of the readme.md
 */
public class GettingStartedExample {
    public static void main (String [] args) {

        String filename = "src/main/resources/datasets/ml100k.data";
        double testUsers = 0.2;
        double testItems = 0.2;
        String separator = "\t";
        long seed = 43;
        DataSet ml100k = new RandomSplitDataSet(filename, testUsers, testItems, separator, seed);

        DataModel datamodel = new DataModel(ml100k);

        PMF pmf = new PMF(datamodel, 10, 100, 0.1, 0.01, 43);
        pmf.fit();

        NMF nmf = new NMF(datamodel, 10, 100, 43);
        nmf.fit();

        QualityMeasure mse;

        mse = new MSE(pmf);
        System.out.println("\nMSE (PMF): " + mse.getScore());

        mse = new MSE(nmf);
        System.out.println("MSE (NMF): " + mse.getScore());

    }
}
