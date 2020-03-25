package es.upm.etsisi.cf4j.examples;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.DataSet;
import es.upm.etsisi.cf4j.data.RandomSplitDataSet;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.qualityMeasure.prediction.MSE;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.Nmf;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.Pmf;

public class GettingStartedExample {
    public static void main (String [] args) {

        String filename = "src/main/resources/datasets/ml1m.dat";
        double testUsers = 0.2;
        double testItems = 0.2;
        String separator = "::";
        long seed = 43;
        DataSet ml1m = new RandomSplitDataSet(filename, testUsers, testItems, separator, seed);

        DataModel datamodel = new DataModel(ml1m);

        Pmf pmf = new Pmf(datamodel, 10, 100, 0.1, 0.01, 43);
        pmf.fit();

        Nmf nmf = new Nmf(datamodel, 10, 100, 43);
        nmf.fit();

        QualityMeasure mse;

        mse = new MSE(pmf);
        System.out.println("\nMSE (PMF): " + mse.getScore());

        mse = new MSE(nmf);
        System.out.println("MSE (NMF): " + mse.getScore());

    }
}
