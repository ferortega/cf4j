package examples;

import cf4j.data.DataModel;
import cf4j.data.RandomSplitDataSet;
import cf4j.process.Processor;
import cf4j.qualityMeasures.MAE;
import cf4j.utils.PrintableQualityMeasure;
import cf4j.utils.Range;

/**
 * In this example we compare MAE using JMSD similarity metric for user to user and item to items
 * collaborative filtering approaches.
 *
 * @author Fernando Ortega
 */
public class Example1 {

	// --- PARAMETERS DEFINITION ------------------------------------------------------------------

	private static String dataset = "../../datasets/MovieLens1M.txt";
	private static double testItems = 0.2; // 20% test items
	private static double testUsers = 0.2; // 20% test users

	private static int [] numberOfNeighbors = Range.ofIntegers(50, 50, 10);
	
	// --------------------------------------------------------------------------------------------

	public static void main (String [] args) {

		// To store experiment results
		String [] approaches = {"user-to-user", "item-to-item"};
		PrintableQualityMeasure mae = new PrintableQualityMeasure ("MAE", numberOfNeighbors, approaches);


		// Load the database
		DataModel dataModel = new DataModel(new RandomSplitDataSet(dataset,testUsers,testItems,"::"));
		//DataModel.getInstance().open(dataset, testUsers, testItems, "::");

		// User to user approach
		Processor processor = new Processor();

		processor.process(new cf4j.algorithms.knn.userToUser.similarities.JMSD(dataModel));

		// For each number of neighbors
		for (int k : numberOfNeighbors) {

			// Compute neighbors
			processor.process(new cf4j.algorithms.knn.userToUser.neighbors.NearestNeighbors(dataModel,k));

			// Compute predictions using Weighted Mean
			processor.process(new cf4j.algorithms.knn.userToUser.aggregationApproaches.WeightedMean(dataModel));

			// Get MAE
			processor.process(new MAE(dataModel));
			mae.putError(k, "user-to-user", dataModel.getStoredData().getDouble("MAE"));
		}


		// Item to item approach
		processor.process(new cf4j.algorithms.knn.itemToItem.similarities.JMSD(dataModel));

		// For each number of neighbors
		for (int k : numberOfNeighbors) {

			// Compute neighbors
			processor.process(new cf4j.algorithms.knn.itemToItem.neighbors.NearestNeighbors(dataModel,k));

			// Compute predictions using DFM
			processor.process(new cf4j.algorithms.knn.itemToItem.aggreagationApproaches.WeightedMean(dataModel));

			// Get MAE
			processor.process(new MAE(dataModel));
			mae.putError(k, "item-to-item", dataModel.getStoredData().getDouble("MAE"));
		}


		// Print results
		mae.print();
	}
}
