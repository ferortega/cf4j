package es.upm.etsisi.cf4j.examples;

import cf4j.algorithms.knn.userSimilarityMetrics.Correlation;
import cf4j.algorithms.knn.userSimilarityMetrics.JMSD;
import cf4j.data.DataModel;
import cf4j.data.RandomSplitDataSet;
import cf4j.process.Parallel;
import cf4j.qualityMeasures.prediction.MAE;
import cf4j.utils.PrintableQualityMeasure;

/**
* This example has been defined in the section 5 (Illustrative Example) of
* the publication related to CF4J.
*
* @author Fernando Ortega
*/
public class PaperExample {

	public static void main (String [] args) {

		String dbPath = "../../datasets/MovieLens1M.txt";
		double testUsers = 0.20; // 20% of test users
		double testItems = 0.20; // 20% of test items

		DataModel dataModel = new DataModel(new RandomSplitDataSet(dbPath, testUsers, testItems, "::"));

		String [] similarityMetrics = {"COR", "JMSD"};
		int [] numberOfNeighbors = {50, 100, 150, 200, 250, 300, 350, 400};

		PrintableQualityMeasure mae = new cf4j.utils.PrintableQualityMeasure("MAE", numberOfNeighbors, similarityMetrics);

		// For each similarity metric
		for (String sm : similarityMetrics) {

			// Compute similarity
			if (sm.equals("COR")) {
				Parallel.getInstance().parallelExec(new Correlation(dataModel));
			}
			else if (sm.equals("JMSD")) {
				Parallel.getInstance().parallelExec(new JMSD(dataModel));
			}

			// For each value of k
			for (int k : numberOfNeighbors) {

				// Find the neighbors
				Parallel.getInstance().parallelExec(new cf4j.algorithms.knn.userToUser.neighbors.NearestNeighbors(dataModel, k));

				// Compute predictions using DFM
				Parallel.getInstance().parallelExec(new cf4j.algorithms.knn.userToUser.aggregationApproaches.DeviationFromMean(dataModel));

				// Compute MAE
				Parallel.getInstance().parallelExec(new MAE(dataModel));
				mae.putError(k, sm, dataModel.getDataBank().getDouble("MAE"));
			}
		}

		//Print the results
		mae.print();
	}
}
