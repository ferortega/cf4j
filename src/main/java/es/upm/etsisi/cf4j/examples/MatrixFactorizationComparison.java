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

	// Same number of iterations for all matrix factorization models
	private static final int numIter = 50;

	// Random seed to guaranty reproducibility of the experiment
	private static final long randomSeed = 43;

	public static void main (String [] args) {

		// Load MovieLens 100K dataset
    	DataSet ml100k = new RandomSplitDataSet("src/main/resources/datasets/ml100k.data", 0.2, 0.2, "\t", randomSeed);
		DataModel datamodel = new DataModel(ml100k);

		// To store results
		PrintableQualityMeasure rmseScores = new PrintableQualityMeasure("RMSE", numFactors);
		PrintableQualityMeasure f1Scores = new PrintableQualityMeasure("F1", numFactors);

		// Evaluate PMF model
		for (int factors : numFactors) {
			Recommender pmf = new PMF(datamodel, factors, numIter, randomSeed);
			pmf.fit();

			QualityMeasure rmse = new RMSE(pmf);
			rmseScores.putScore(factors, "PMF", rmse.getScore());

			QualityMeasure f1 = new F1(pmf,10, 4);
			f1Scores.putScore(factors, "PMF", f1.getScore());
		}

		// Evaluate BNMF model
		for (int factors : numFactors) {
			Recommender bnmf = new BNMF(datamodel, factors, numIter, 0.2, 10, randomSeed);
			bnmf.fit();

			QualityMeasure rmse = new RMSE(bnmf);
			rmseScores.putScore(factors, "BNMF", rmse.getScore());

			QualityMeasure f1 = new F1(bnmf,10, 4);
			f1Scores.putScore(factors, "BNMF", f1.getScore());
		}

		// Evaluate BiasedMF model
		for (int factors : numFactors) {
			Recommender biasedmf = new BiasedMF(datamodel, factors, numIter, randomSeed);
			biasedmf.fit();

			QualityMeasure rmse = new RMSE(biasedmf);
			rmseScores.putScore(factors, "BiasedMF", rmse.getScore());

			QualityMeasure f1 = new F1(biasedmf,10, 4);
			f1Scores.putScore(factors, "BiasedMF", f1.getScore());
		}

		// Evaluate NMF model
		for (int factors : numFactors) {
			Recommender nmf = new NMF(datamodel, factors, numIter, randomSeed);
			nmf.fit();

			QualityMeasure rmse = new RMSE(nmf);
			rmseScores.putScore(factors, "NMF", rmse.getScore());

			QualityMeasure f1 = new F1(nmf,10, 4);
			f1Scores.putScore(factors, "NMF", f1.getScore());
		}

		// Evaluate CLiMF model
		for (int factors : numFactors) {
			Recommender climf = new CLiMF(datamodel, factors, numIter, randomSeed);
			climf.fit();

			QualityMeasure rmse = new RMSE(climf);
			rmseScores.putScore(factors, "CLiMF", rmse.getScore());

			QualityMeasure f1 = new F1(climf,10, 4);
			f1Scores.putScore(factors, "CLiMF", f1.getScore());
		}

		// Evaluate SVDPlusPlus model
		for (int factors : numFactors) {
			Recommender svdPlusPlus = new SVDPlusPlus(datamodel, factors, numIter, randomSeed);
			svdPlusPlus.fit();

			QualityMeasure rmse = new RMSE(svdPlusPlus);
			rmseScores.putScore(factors, "SVDPlusPlus", rmse.getScore());

			QualityMeasure f1 = new F1(svdPlusPlus,10, 4);
			f1Scores.putScore(factors, "SVDPlusPlus", f1.getScore());
		}

		// Evaluate HPF model
		for (int factors : numFactors) {
			Recommender hpf = new HPF(datamodel, factors, numIter, randomSeed);
			hpf.fit();

			QualityMeasure rmse = new RMSE(hpf);
			rmseScores.putScore(factors, "HPF", rmse.getScore());

			QualityMeasure f1 = new F1(hpf,10, 4);
			f1Scores.putScore(factors, "HPF", f1.getScore());
		}

		// Evaluate URP model
		for (int factors : numFactors) {
			double[] ratings = {1.0, 2.0, 3.0, 4.0, 5.0};
			Recommender urp = new URP(datamodel, factors, ratings, numIter, randomSeed);
			urp.fit();

			QualityMeasure rmse = new RMSE(urp);
			rmseScores.putScore(factors, "URP", rmse.getScore());

			QualityMeasure f1 = new F1(urp,10, 4);
			f1Scores.putScore(factors, "URP", f1.getScore());
		}

		// Print results
		rmseScores.print();
		f1Scores.print();
	}
}
