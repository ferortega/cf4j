package es.upm.etsisi.cf4j.examples;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.DataSet;
import es.upm.etsisi.cf4j.data.RandomSplitDataSet;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.qualityMeasure.prediction.RMSE;
import es.upm.etsisi.cf4j.qualityMeasure.recommendation.F1;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.*;
import es.upm.etsisi.cf4j.util.PrintableQualityMeasure;
import es.upm.etsisi.cf4j.util.Range;

/**
 * In this example we compare the RMSE and F1 for different matrix factorization models varying the number of
 * latent factors
 */
public class MatrixFactorizationComparison {

	// Grid search over number of factors hyper-parameter
	private static final int[] numFactors = Range.ofIntegers(5,5,5);

	// Matrix factorization models to be evaluated
	private static final String[] methods = {"PMF", "BNMF", "BiasedMF", "NMF", "CLiMF", "SVD++", "HPF", "URP"};

	// Same number of iterations for all matrix factorization models
	private static final int numIter = 50;

	// Random seed to guaranty reproducibility of the experiment
	private static final long randomSeed = 43;

	public static void main (String [] args) {

		// Load MovieLens 100K dataset
    	DataSet ml1m = new RandomSplitDataSet("src/main/resources/datasets/ml100k.data", 0.2, 0.2, "\t", 43);
		DataModel datamodel = new DataModel(ml1m);

		// To store results
		PrintableQualityMeasure rmseScores = new PrintableQualityMeasure("RMSE", numFactors, methods);
		PrintableQualityMeasure f1Scores = new PrintableQualityMeasure("F1", numFactors, methods);

		// Fit models
		for (String method : methods) {
			for (int factors : numFactors) {

				Recommender recommender;
				switch (method) {
					case "PMF":
						recommender = new PMF(datamodel, factors, numIter, randomSeed);
						break;
					case "BNMF":
						recommender = new BNMF(datamodel, factors, numIter, 0.2, 10, randomSeed);
						break;
					case "BiasedMF":
						recommender = new BiasedMF(datamodel, factors, numIter, randomSeed);
						break;
					case "NMF":
						recommender = new NMF(datamodel, factors, numIter, randomSeed);
						break;
					case "CLiMF":
						recommender = new CLiMF(datamodel, factors, numIter, randomSeed);
						break;
					case "SVD++":
						recommender = new SVDPlusPlus(datamodel, factors, numIter, randomSeed);
						break;
					case "HPF":
						recommender = new HPF(datamodel, factors, numIter, randomSeed);
						break;
					case "URP":
						double[] ratings = {1.0, 2.0, 3.0, 4.0, 5.0};
						recommender = new URP(datamodel, factors, ratings, numIter, randomSeed);
						break;
					default:
						throw new IllegalStateException("Unexpected value: " + method);
				}

				recommender.fit();

				QualityMeasure rmse = new RMSE(recommender);
				rmseScores.putError(factors, method, rmse.getScore());

				QualityMeasure f1 = new F1(recommender,10, 4);
				f1Scores.putError(factors, method, f1.getScore());
			}
		}

		// Print results
		rmseScores.print();
		f1Scores.print();
	}
}
