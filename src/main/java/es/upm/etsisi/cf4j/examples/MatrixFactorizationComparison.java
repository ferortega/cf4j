package es.upm.etsisi.cf4j.examples;

import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;
import com.github.ferortega.cf4j.recommender.matrixFactorization.*;
import es.upm.etsisi.cf4j.util.PrintableQualityMeasure;
import es.upm.etsisi.cf4j.util.Range;
import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.DataSet;
import es.upm.etsisi.cf4j.data.RandomSplitDataSet;
import es.upm.etsisi.cf4j.qualityMeasure.prediction.RMSE;
import es.upm.etsisi.cf4j.qualityMeasure.recommendation.F1;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.*;

import java.io.IOException;

/**
 * In this example we compare the RMSE and F1 for different matrix factorization models varying the number of
 * latent factors.
 */
public class MatrixFactorizationComparison {

	// Grid search over number of factors hyper-parameter
	private static final int[] numFactors = Range.ofIntegers(5,5,5);

	// Same number of iterations for all matrix factorization models
	private static final int numIter = 50;

	// Random seed to guaranty reproducibility of the experiment
	private static final long randomSeed = 43;

	public static void main (String [] args) throws IOException {

		// Step 1: Preparing the dataset to be splitted in two parts: training and test (Load MovieLens 100K dataset)
		DataSet ml100k = new RandomSplitDataSet("src/main/resources/datasets/ml100k.data", 0.2, 0.2, "\t", randomSeed);

		// Step 2: Storing the data in the DataModel to be efficiently accessed by the recommenders
		DataModel datamodel = new DataModel(ml100k);

		// To store results
		PrintableQualityMeasure rmseScores = new PrintableQualityMeasure("RMSE", numFactors);
		PrintableQualityMeasure f1Scores = new PrintableQualityMeasure("F1", numFactors);

		// Step 3.a: Generating an specific recommender (Probabilistic matrix factorization) with different factors
		for (int factors : numFactors) {
			Recommender pmf = new PMF(datamodel, factors, numIter, randomSeed);
			pmf.fit();

			// Step 4.a: Setting up a RMSE and F1 quality measures with PMF recommender.
			QualityMeasure rmse = new RMSE(pmf);
			rmseScores.putScore(factors, "PMF", rmse.getScore());

			QualityMeasure f1 = new F1(pmf,10, 4);
			f1Scores.putScore(factors, "PMF", f1.getScore());
		}

		// Step 3.b: Generating an specific recommender (Bayesian Non-negative Matrix Factorization) with different factors
		for (int factors : numFactors) {
			Recommender bnmf = new BNMF(datamodel, factors, numIter, 0.2, 10, randomSeed);
			bnmf.fit();

			// Step 4.b: Setting up a RMSE and F1 quality measures with BNMF recommender.
			QualityMeasure rmse = new RMSE(bnmf);
			rmseScores.putScore(factors, "BNMF", rmse.getScore());

			QualityMeasure f1 = new F1(bnmf,10, 4);
			f1Scores.putScore(factors, "BNMF", f1.getScore());
		}

		// Step 3.c: Generating an specific recommender (Biased Matrix Factorization) with different factors
		for (int factors : numFactors) {
			Recommender biasedmf = new BiasedMF(datamodel, factors, numIter, randomSeed);
			biasedmf.fit();

			// Step 4.c: Setting up a RMSE and F1 quality measures with BiasedMF recommender.
			QualityMeasure rmse = new RMSE(biasedmf);
			rmseScores.putScore(factors, "BiasedMF", rmse.getScore());

			QualityMeasure f1 = new F1(biasedmf,10, 4);
			f1Scores.putScore(factors, "BiasedMF", f1.getScore());
		}

		// Step 3.d: Generating an specific recommender (Non-negative Matrix Factorization) with different factors
		for (int factors : numFactors) {
			Recommender nmf = new NMF(datamodel, factors, numIter, randomSeed);
			nmf.fit();

			// Step 4.d: Setting up a RMSE and F1 quality measures with NMF recommender.
			QualityMeasure rmse = new RMSE(nmf);
			rmseScores.putScore(factors, "NMF", rmse.getScore());

			QualityMeasure f1 = new F1(nmf,10, 4);
			f1Scores.putScore(factors, "NMF", f1.getScore());
		}

		// Step 3.f: Generating an specific recommender (Collaborative Less-is-More Filtering) with different factors
		for (int factors : numFactors) {
			Recommender climf = new CLiMF(datamodel, factors, numIter, randomSeed);
			climf.fit();

			// Step 4.f: Setting up a RMSE and F1 quality measures with CLiMF recommender.
			QualityMeasure rmse = new RMSE(climf);
			rmseScores.putScore(factors, "CLiMF", rmse.getScore());

			QualityMeasure f1 = new F1(climf,10, 4);
			f1Scores.putScore(factors, "CLiMF", f1.getScore());
		}

		// Step 3.g: Generating an specific recommender (SVD++) with different factors
		for (int factors : numFactors) {
			Recommender svdPlusPlus = new SVDPlusPlus(datamodel, factors, numIter, randomSeed);
			svdPlusPlus.fit();

			// Step 4.g: Setting up a RMSE and F1 quality measures with SVDPlusPlus recommender.
			QualityMeasure rmse = new RMSE(svdPlusPlus);
			rmseScores.putScore(factors, "SVDPlusPlus", rmse.getScore());

			QualityMeasure f1 = new F1(svdPlusPlus,10, 4);
			f1Scores.putScore(factors, "SVDPlusPlus", f1.getScore());
		}

		// Step 3.h: Generating an specific recommender (Hierarchical Poisson Factorization) with different factors
		for (int factors : numFactors) {
			Recommender hpf = new HPF(datamodel, factors, numIter, randomSeed);
			hpf.fit();

			// Step 4.h: Setting up a RMSE and F1 quality measures with HPF recommender.
			QualityMeasure rmse = new RMSE(hpf);
			rmseScores.putScore(factors, "HPF", rmse.getScore());

			QualityMeasure f1 = new F1(hpf,10, 4);
			f1Scores.putScore(factors, "HPF", f1.getScore());
		}

		// Step 3.i: Generating an specific recommender (User Rating Profiles) with different factors
		for (int factors : numFactors) {
			double[] ratings = {1.0, 2.0, 3.0, 4.0, 5.0};
			Recommender urp = new URP(datamodel, factors, ratings, numIter, randomSeed);
			urp.fit();

			// Step 4.i: Setting up a RMSE and F1 quality measures with URP recommender.
			QualityMeasure rmse = new RMSE(urp);
			rmseScores.putScore(factors, "URP", rmse.getScore());

			QualityMeasure f1 = new F1(urp,10, 4);
			f1Scores.putScore(factors, "URP", f1.getScore());
		}

		// Step 5: Printing the results
		rmseScores.print();
		f1Scores.print();
	}
}
