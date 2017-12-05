package examples;

import cf4j.Kernel;
import cf4j.Processor;
import cf4j.model.matrixFactorization.Nnmf;
import cf4j.model.matrixFactorization.Pmf;
import cf4j.model.predictions.FactorizationPrediction;
import cf4j.qualityMeasures.MAE;
import cf4j.qualityMeasures.Precision;

/**
 * Compare MAE and Precision of PMF and NNMF. 
 * @author Fernando Ortega
 */
public class Example3 {

	// --- PARAMETERS DEFINITION ------------------------------------------------------------------

	private static String dataset = "datasets/movielens/ratings.dat"; 
	private static double testItems = 0.2; // 20% test items
	private static double testUsers = 0.2; // 20% test users

	private static int numRecommendations = 10;
	private static double threshold = 4.0;

	private static int pmf_numTopics = 15;
	private static int pmf_numIters = 50;
	private static double pmf_lambda = 0.055;

	private static int nnmf_numTopics = 6;
	private static int nnmf_numIters = 50;
	private static double nnmf_alpha = 0.8;
	private static double nnmf_beta = 5;

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


		// NNMF
		Nnmf nnmf = new Nnmf (nnmf_numTopics, nnmf_numIters, nnmf_alpha, nnmf_beta);
		nnmf.train();

		Processor.getInstance().testUsersProcess(new FactorizationPrediction(nnmf));

		System.out.println("\nPMF:");

		Processor.getInstance().testUsersProcess(new MAE());
		System.out.println("- MAE: " + Kernel.gi().getQualityMeasure("MAE"));

		Processor.getInstance().testUsersProcess(new Precision(numRecommendations, threshold));
		System.out.println("- Precision: " + Kernel.gi().getQualityMeasure("Precision"));
	}
}
