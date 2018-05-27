package examples;

import cf4j.Kernel;
import cf4j.Processor;
import cf4j.knn.userToUser.aggregationApproaches.*;
import cf4j.knn.userToUser.neighbors.*;
import cf4j.knn.userToUser.similarities.*;
import cf4j.qualityMeasures.Coverage;
import cf4j.qualityMeasures.F1;
import cf4j.qualityMeasures.MAE;
import cf4j.qualityMeasures.Precision;
import cf4j.qualityMeasures.Recall;
import cf4j.utils.PrintableQualityMeasure;
import cf4j.utils.Range;

/**
 * In this example we compare the similarity metrics COR, MSD, Jaccard and JMSD using
 * the quality measures MAE, Coverage, Precision, Recall and F1.
 *
 * @author Fernando Ortega
 */
public class Example2 {

	// --- PARAMETERS DEFINITION ------------------------------------------------------------------

	private static String dataset = "../../datasets/MovieLens1M.txt";
	private static double testItems = 0.2; // 20% test items
	private static double testUsers = 0.2; // 20% test users

	private static int [] numberOfNeighbors = Range.ofIntegers(50, 50, 10);
	private static int [] numberOfRecommendations = Range.ofIntegers(1, 1, 10);

	private static double precisionRecallThreshold = 5;
	private static int precisionRecallK = 200;

	private static String [] similarityMetrics = {"COR", "MSD", "JAC", "JMSD"};


	// --------------------------------------------------------------------------------------------

	public static void main (String [] args) {

		// To store the quality measures results
		PrintableQualityMeasure mae
			= new PrintableQualityMeasure ("MAE", numberOfNeighbors, similarityMetrics);

		PrintableQualityMeasure coverage
			= new PrintableQualityMeasure ("Coverage", numberOfNeighbors, similarityMetrics);

		PrintableQualityMeasure precision
			= new PrintableQualityMeasure ("Precision", numberOfRecommendations, similarityMetrics);

		PrintableQualityMeasure recall
			= new PrintableQualityMeasure ("Recall", numberOfRecommendations, similarityMetrics);

		PrintableQualityMeasure f1
			= new PrintableQualityMeasure ("F1", numberOfRecommendations, similarityMetrics);

		// Load the database
		Kernel.getInstance().open(dataset, testUsers, testItems, "::");

		// Test each similarity metric
		for (String sm : similarityMetrics) {

			// Compute similarity
			if (sm.equals("COR")) {
				Processor.getInstance().testUsersProcess(new MetricCorrelation());
			}
			else if (sm.equals("MSD")) {
				Processor.getInstance().testUsersProcess(new MetricMSD());
			}
			else if (sm.equals("JAC")) {
				Processor.getInstance().testUsersProcess(new MetricJaccard());
			}
			else if (sm.equals("JMSD")) {
				Processor.getInstance().testUsersProcess(new MetricJMSD());
			}

			// For each number of neighbors
			for (int k : numberOfNeighbors) {

				// Compute neighbors
				Processor.getInstance().testUsersProcess(new Neighbors(k));

				// Compute predictions using DFM
				Processor.getInstance().testUsersProcess(new DeviationFromMean());

				// Get MAE
				Processor.getInstance().testUsersProcess(new MAE());
				mae.putError(k, sm, Kernel.gi().getQualityMeasure("MAE"));

				// Get Coverage
				Processor.getInstance().testUsersProcess(new Coverage());
				coverage.putError(k, sm, Kernel.gi().getQualityMeasure("Coverage"));
			}

			// For each number of recommendations
			Processor.getInstance().testUsersProcess(new Neighbors(precisionRecallK));
			Processor.getInstance().testUsersProcess(new DeviationFromMean());

			for (int n : numberOfRecommendations) {

				// Get precision
				Processor.getInstance().testUsersProcess(new Precision(n, precisionRecallThreshold));
				precision.putError(n, sm, Kernel.gi().getQualityMeasure("Precision"));

				// Get recall
				Processor.getInstance().testUsersProcess(new Recall(n, precisionRecallThreshold));
				recall.putError(n, sm, Kernel.gi().getQualityMeasure("Recall"));

				// Get F1 score
				Processor.getInstance().testUsersProcess(new F1(n, precisionRecallThreshold));
				f1.putError(n, sm, Kernel.gi().getQualityMeasure("F1"));
			}


			// Print results
			mae.print();
			coverage.print();
			precision.print();
			recall.print();
			f1.print();
		}
	}
}
