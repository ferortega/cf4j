package examples;

import cf4j.Kernel;
import cf4j.Processor;
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

		Kernel.getInstance().open(dbPath, testUsers, testItems, "::");

		String [] similarityMetrics = {"COR", "JMSD"};
		int [] numberOfNeighbors = {50, 100, 150, 200, 250, 300, 350, 400};

		PrintableQualityMeasure mae = new cf4j.utils.PrintableQualityMeasure("MAE", numberOfNeighbors, similarityMetrics);

		// For each similarity metric
		for (String sm : similarityMetrics) {

			// Compute similarity
			if (sm.equals("COR")) {
				Processor.getInstance().testUsersProcess(new cf4j.knn.userToUser.similarities.MetricCorrelation());
			}
			else if (sm.equals("JMSD")) {
				Processor.getInstance().testUsersProcess(new cf4j.knn.userToUser.similarities.MetricJMSD());
			}

			// For each value of k
			for (int k : numberOfNeighbors) {

				// Find the neighbors
				Processor.getInstance().testUsersProcess(new cf4j.knn.userToUser.neighbors.Neighbors(k));

				// Compute predictions using DFM
				Processor.getInstance().testUsersProcess(new cf4j.knn.userToUser.aggregationApproaches.DeviationFromMean());

				// Compute MAE
				Processor.getInstance().testUsersProcess(new cf4j.qualityMeasures.MAE());
				mae.putError(k, sm, Kernel.gi().getQualityMeasure("MAE"));
			}
		}

		// Print the results
		mae.print();
	}
}
