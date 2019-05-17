package examples;

import cf4j.algorithms.model.matrixFactorization.Bmf;
import cf4j.algorithms.model.matrixFactorization.Pmf;
import cf4j.algorithms.model.predictions.FactorizationPrediction;
import cf4j.data.DataModel;
import cf4j.data.RandomSplitDataSet;
import cf4j.process.Processor;
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
		DataModel dataModel = new DataModel(new RandomSplitDataSet(dataset,testUsers,testItems,"::"));

		Processor processor = new Processor();

		// PMF
		Pmf pmf = new Pmf (dataModel, pmf_numTopics, pmf_numIters, pmf_lambda);
		pmf.train();

		processor.process(new FactorizationPrediction(dataModel, pmf));

		System.out.println("\nPMF:");

		processor.process(new MAE(dataModel));
		System.out.println("- MAE: " + dataModel.getDataBank().getDouble("MAE"));

		processor.process(new Precision(dataModel, numRecommendations, threshold));
		System.out.println("- Precision: " + dataModel.getDataBank().getDouble("Precision"));


		// BMF
		Bmf bmf = new Bmf (dataModel, bmf_numTopics, bmf_numIters, bmf_alpha, bmf_beta);
		bmf.train();

		processor.process(new FactorizationPrediction(dataModel, bmf));

		System.out.println("\nBMF:");

		processor.process(new MAE(dataModel));
		System.out.println("- MAE: " + dataModel.getDataBank().getDouble("MAE"));

		processor.process(new Precision(dataModel, numRecommendations, threshold));
		System.out.println("- Precision: " + dataModel.getDataBank().getDouble("Precision"));
	}
}
