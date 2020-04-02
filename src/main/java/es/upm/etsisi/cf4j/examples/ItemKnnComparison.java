package es.upm.etsisi.cf4j.examples;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.DataSet;
import es.upm.etsisi.cf4j.data.RandomSplitDataSet;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.qualityMeasure.prediction.MSLE;
import es.upm.etsisi.cf4j.qualityMeasure.recommendation.NDCG;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.recommender.knn.ItemKNN;
import es.upm.etsisi.cf4j.recommender.knn.itemSimilarityMetric.*;
import es.upm.etsisi.cf4j.util.PrintableQualityMeasure;
import es.upm.etsisi.cf4j.util.Range;

import java.io.IOException;
import java.util.ArrayList;

/**
 * In this example we compare the MSLE and nDCG quality measures scores for different similarity metrics applied to
 * item-to-item knn based collaborative filtering. Each similarity metric is tested with different number of neighbors.
 */
public class ItemKnnComparison {

	// Grid search over number of neighbors hyper-parameter
	private static final int[] numNeighbors = Range.ofIntegers(100,50,5);

	// Fixed aggregation approach
	private static final ItemKNN.AggregationApproach aggregationApproach = ItemKNN.AggregationApproach.MEAN;

	// Random seed to guaranty reproducibility of the experiment
	private static final long randomSeed = 43;

	public static void main (String [] args) throws IOException {

		// Step 1: Preparing the dataset to be splitted in two parts: training and test (Load MovieLens 100K dataset).
		DataSet ml1m = new RandomSplitDataSet("src/main/resources/datasets/ml100k.data", 0.2, 0.2, "\t", randomSeed);

		// Step 2: Storing the data in the DataModel to be efficiently accessed by the recommenders.
		DataModel datamodel = new DataModel(ml1m);

		// Dataset parameters.
		double[] relevantRatings = {3, 4, 5};
		double[] notRelevantRatings = {1, 2};

		// To store results.
		PrintableQualityMeasure msleScores = new PrintableQualityMeasure("MSLE", numNeighbors);
		PrintableQualityMeasure ndcgScores = new PrintableQualityMeasure("NDCG", numNeighbors);

		// Create similarity metrics.
		ArrayList<ItemSimilarityMetric> metrics = new ArrayList<>();
		metrics.add(new AdjustedCosine());
		metrics.add(new Correlation());
		metrics.add(new Cosine());
		metrics.add(new Jaccard());
		metrics.add(new JMSD());
		metrics.add(new MSD());
		metrics.add(new PIP());
		metrics.add(new Singularities(relevantRatings, notRelevantRatings));
		metrics.add(new SpearmanRank());

		// Evaluate ItemKNN recommender
		for (ItemSimilarityMetric metric : metrics) {
			String metricName = metric.getClass().getSimpleName();

			for (int k : numNeighbors) {
				// Step 3: Generating an specific recommender (ItemKNN) with a number of neighbors applying different metrics.
				Recommender knn = new ItemKNN(datamodel, k, metric, aggregationApproach);
				knn.fit();

				// Step 4: Setting up a MSLE and nDCG quality measures with ItemKNN recommender.
				QualityMeasure msle = new MSLE(knn);
				msleScores.putScore(k, metricName, msle.getScore());

				QualityMeasure ndcg = new NDCG(knn,10);
				ndcgScores.putScore(k, metricName, ndcg.getScore());
			}
		}

		// Step 5: Printing the results.
		msleScores.print();
		ndcgScores.print();
	}
}
