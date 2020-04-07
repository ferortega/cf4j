package com.github.ferortega.cf4j.examples;

import com.github.ferortega.cf4j.qualityMeasure.QualityMeasure;
import com.github.ferortega.cf4j.qualityMeasure.recommendation.Precision;
import com.github.ferortega.cf4j.qualityMeasure.recommendation.Recall;
import com.github.ferortega.cf4j.recommender.Recommender;
import com.github.ferortega.cf4j.recommender.knn.UserKNN;
import com.github.ferortega.cf4j.recommender.knn.userSimilarityMetric.*;
import com.github.ferortega.cf4j.util.PrintableQualityMeasure;
import com.github.ferortega.cf4j.util.Range;
import com.github.ferortega.cf4j.data.DataModel;
import com.github.ferortega.cf4j.data.DataSet;
import com.github.ferortega.cf4j.data.RandomSplitDataSet;
import com.github.ferortega.cf4j.qualityMeasure.prediction.Coverage;
import com.github.ferortega.cf4j.qualityMeasure.prediction.MAE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * In this example we compare the MAE, Coverage, Precision and Recall quality measures scores for different similarity
 * metrics applied to user-to-user knn based collaborative filtering. Each similarity metric is tested with different
 * number of neighbors.
 */
public class UserKnnComparison {

	// Grid search over number of neighbors hyper-parameter
	private static final int[] numNeighbors = Range.ofIntegers(100,50,5);

	// Fixed aggregation approach
	private static final UserKNN.AggregationApproach aggregationApproach = UserKNN.AggregationApproach.DEVIATION_FROM_MEAN;

	// Random seed to guaranty reproducibility of the experiment
	private static final long randomSeed = 43;

	public static void main (String [] args) throws IOException {

		// Step 1: Preparing the dataset to be splitted in two parts: training and test (Load MovieLens 100K dataset)
		DataSet ml1m = new RandomSplitDataSet("src/main/resources/datasets/ml100k.data", 0.2, 0.2, "\t", randomSeed);

		// Step 2: Storing the data in the DataModel to be efficiently accessed by the recommenders
		DataModel datamodel = new DataModel(ml1m);

		// Step 3 and 4: Generating an ItemKNN recommender which uses different item similarity metrics:

		// Dataset parameters
		double[] relevantRatings = {3, 4, 5};
		double[] notRelevantRatings = {1, 2};

		// To store results
		PrintableQualityMeasure maeScores = new PrintableQualityMeasure("MAE", numNeighbors);
		PrintableQualityMeasure coverageScores = new PrintableQualityMeasure("Coverage", numNeighbors);
		PrintableQualityMeasure precisionScores = new PrintableQualityMeasure("Precision", numNeighbors);
		PrintableQualityMeasure recallScores = new PrintableQualityMeasure("Recall", numNeighbors);

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
		metrics.add(new Singularities(relevantRatings, notRelevantRatings));
		metrics.add(new SpearmanRank());

		// Evaluate UserKNN recommender
		for (UserSimilarityMetric metric : metrics) {
			String metricName = metric.getClass().getSimpleName();

			for (int k : numNeighbors) {
				// Step 3: Generating an specific recommender (UserKNN) with a number of neighbors applying different metrics.
				Recommender knn = new UserKNN(datamodel, k, metric, aggregationApproach);
				knn.fit();

				// Step 4: Setting up a MAE, Coverage, Precision, and Recall quality measures with UserKNN recommender.
				QualityMeasure mae = new MAE(knn);
				maeScores.putScore(k, metricName, mae.getScore());

				QualityMeasure coverage = new Coverage(knn);
				coverageScores.putScore(k, metricName, coverage.getScore());

				QualityMeasure precision = new Precision(knn,10, 4);
				precisionScores.putScore(k, metricName, precision.getScore());

				QualityMeasure recall = new Recall(knn,10, 4);
				recallScores.putScore(k, metricName, recall.getScore());
			}
		}

		// Step 5: Printing the results
		maeScores.print();
		coverageScores.print();
		precisionScores.print();
		recallScores.print();
	}
}
