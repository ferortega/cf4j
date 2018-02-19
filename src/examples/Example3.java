package examples;

import cf4j.Kernel;
import cf4j.Processor;
import cf4j.model.matrixFactorization.Bmf;
import cf4j.model.matrixFactorization.Pmf;
import cf4j.model.predictions.FactorizationPrediction;
import cf4j.qualityMeasures.MAE;
import cf4j.qualityMeasures.Precision;

/**
 * Compare MAE and Precision of PMF and BMF.
 * @author Fernando Ortega
 */
public class Example3 {

	// --- PARAMETERS DEFINITION ------------------------------------------------------------------

	private static String dataset = "../../datasets/MovieLens1M.txt";
	private static double testItems = 0.2; // 20% test items
	private static double testUsers = 0.2; // 20% test users

	private static int numRecommendations = 10;
	private static double threshold = 4.0;

	private static int pmf_numTopics = 15;
	private static int pmf_numIters = 50;
	private static double pmf_lambda = 0.055;

	private static int bmf_numTopics = 6;
	private static int bmf_numIters = 50;
	private static double bmf_alpha = 0.8;
	private static double bmf_beta = 5;

	// --------------------------------------------------------------------------------------------

	public static void main (String [] args) {

		// Load the database
		Kernel.getInstance().open(dataset, testUsers, testItems, "::");


		// PMF
		Pmf pmf = new Pmf (pmf_numTopics, pmf_numIters, pmf_lambda);
		pmf.train();

		Processor.getInstance().testUsersProcess(new FactorizationPrediction(pmf));

		System.out.println("\nPMF:");

		Processor.getInstance().testUsersProcess(new MAE());
		System.out.println("- MAE: " + Kernel.gi().getQualityMeasure("MAE"));

		Processor.getInstance().testUsersProcess(new Precision(numRecommendations, threshold));
		System.out.println("- Precision: " + Kernel.gi().getQualityMeasure("Precision"));


		// BMF
		Bmf bmf = new Bmf (bmf_numTopics, bmf_numIters, bmf_alpha, bmf_beta);
		bmf.train();

		Processor.getInstance().testUsersProcess(new FactorizationPrediction(bmf));

		System.out.println("\nBMF:");

		Processor.getInstance().testUsersProcess(new MAE());
		System.out.println("- MAE: " + Kernel.gi().getQualityMeasure("MAE"));

		Processor.getInstance().testUsersProcess(new Precision(numRecommendations, threshold));
		System.out.println("- Precision: " + Kernel.gi().getQualityMeasure("Precision"));
	}
}
