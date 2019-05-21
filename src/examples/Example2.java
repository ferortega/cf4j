package examples;

import cf4j.algorithms.knn.userToUser.aggregationApproaches.*;
import cf4j.algorithms.knn.userToUser.neighbors.*;
import cf4j.algorithms.knn.userToUser.similarities.*;
import cf4j.data.DataModel;
import cf4j.data.RandomSplitDataSet;
import cf4j.process.Processor;
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
		DataModel dataModel = new DataModel(new RandomSplitDataSet(dataset,testUsers,testItems,"::"));

		// Test each similarity metric
		for (String sm : similarityMetrics) {

			// Compute similarity
			if (sm.equals("COR")) {
				Processor.getInstance().parallelExec(new Correlation(dataModel));
			}
			else if (sm.equals("MSD")) {
				Processor.getInstance().parallelExec(new MSD(dataModel));
			}
			else if (sm.equals("JAC")) {
				Processor.getInstance().parallelExec(new Jaccard(dataModel));
			}
			else if (sm.equals("JMSD")) {
				Processor.getInstance().parallelExec(new JMSD(dataModel));
			}

			// For each number of neighbors
			for (int k : numberOfNeighbors) {

				// Compute neighbors
				Processor.getInstance().parallelExec(new NearestNeighbors(dataModel, k));

				// Compute predictions using DFM
				Processor.getInstance().parallelExec(new DeviationFromMean(dataModel));

				// Get MAE
				Processor.getInstance().parallelExec(new MAE(dataModel));
				mae.putError(k, sm, dataModel.getDataBank().getDouble("MAE"));

				// Get Coverage
				Processor.getInstance().parallelExec(new Coverage(dataModel));
				coverage.putError(k, sm, dataModel.getDataBank().getDouble("Coverage"));
			}

			// For each number of recommendations
			Processor.getInstance().parallelExec(new NearestNeighbors(dataModel, precisionRecallK));
			Processor.getInstance().parallelExec(new DeviationFromMean(dataModel));

			for (int n : numberOfRecommendations) {

				// Get precision
				Processor.getInstance().parallelExec(new Precision(dataModel,n, precisionRecallThreshold));
				precision.putError(n, sm, dataModel.getDataBank().getDouble("Precision"));

				// Get recall
				Processor.getInstance().parallelExec(new Recall(dataModel,n, precisionRecallThreshold));
				recall.putError(n, sm, dataModel.getDataBank().getDouble("Recall"));

				// Get F1 score
				Processor.getInstance().parallelExec(new F1(dataModel,n, precisionRecallThreshold));
				f1.putError(n, sm, dataModel.getDataBank().getDouble("F1"));
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
