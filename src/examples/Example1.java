package examples;

import cf4j.data.DataModel;
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
		DataModel.getInstance().open(dataset, testUsers, testItems, "::");


		// User to user approach
		Processor.getInstance().testUsersProcess(new cf4j.algorithms.knn.userToUser.similarities.JMSD());

		// For each number of neighbors
		for (int k : numberOfNeighbors) {

			// Compute neighbors
			Processor.getInstance().testUsersProcess(new cf4j.algorithms.knn.userToUser.neighbors.NearestNeighbors(k));

			// Compute predictions using Weighted Mean
			Processor.getInstance().testUsersProcess(new cf4j.algorithms.knn.userToUser.aggregationApproaches.WeightedMean());

			// Get MAE
			Processor.getInstance().testUsersProcess(new MAE());
			mae.putError(k, "user-to-user", DataModel.gi().getQualityMeasure("MAE"));
		}


		// Item to item approach
		Processor.getInstance().testItemsProcess(new cf4j.algorithms.knn.itemToItem.similarities.JMSD());

		// For each number of neighbors
		for (int k : numberOfNeighbors) {

			// Compute neighbors
			Processor.getInstance().testItemsProcess(new cf4j.algorithms.knn.itemToItem.neighbors.Neighbors(k));

			// Compute predictions using DFM
			Processor.getInstance().testUsersProcess(new cf4j.algorithms.knn.itemToItem.aggreagationApproaches.WeightedMean());

			// Get MAE
			Processor.getInstance().testUsersProcess(new MAE());
			mae.putError(k, "item-to-item", DataModel.gi().getQualityMeasure("MAE"));
		}


		// Print results
		mae.print();
	}
}
