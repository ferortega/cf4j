package cf4j.knn.itemToItem.aggreagationApproaches;

import cf4j.Item;
import cf4j.Kernel;
import cf4j.TestItem;
import cf4j.TestUser;
import cf4j.TestUsersPartible;

/**
 * <p>This class computes the prediction of the test users' test items. The results are 
 * saved in double array on the hashmap of each test user with the key "predictions". This 
 * array overlaps with the test items' array of the test users. For example, the prediction
 * retrieved with the method testUser.getPredictions()[i] is the prediction of the item
 * testUser.getTestItems()[i].</p>
 * 
 * <p>This class uses weighted average as method to combine the ratings of the items more
 * similar to the active one.</p>
 * 
 * @author Fernando Ortega
 */
public class WeightedMean implements TestUsersPartible {
	
	/**
	 * Minimum similarity computed
	 */
	private double minSim;
	
	/**
	 * Maximum similarity computed
	 */
	private double maxSim;

	@Override
	public void beforeRun() { 
		this.maxSim = Double.MIN_VALUE;
		this.minSim = Double.MAX_VALUE;
		
		for (TestItem testItem : Kernel.gi().getTestItems()) {
			for (double m : testItem.getSimilarities()) {
				if (!Double.isInfinite(m)) {
					if (m < this.minSim) this.minSim = m;
					if (m > this.maxSim) this.maxSim = m;
				}
			}
		}
	}

	@Override
	public void run (int testUserIndex) {

		TestUser testUser = Kernel.gi().getTestUsers()[testUserIndex];
				
		int numRatings = testUser.getNumberOfTestRatings();
		double [] predictions = new double [numRatings];
		
		for (int testItemIndex = 0; testItemIndex < numRatings; testItemIndex++) {
			
			int itemCode = testUser.getTestItems()[testItemIndex];
			TestItem item = Kernel.gi().getTestItemByCode(itemCode);
			
			double [] similarities = item.getSimilarities();
			int [] neighbors = item.getNeighbors();
			
			double sumSimilarities = 0;
			
			for (int n = 0; n < neighbors.length; n++) {
				if (neighbors[n] == -1) break; // Neighbors array are filled with -1 when no more neighbors exists
				
				int itemIndex = neighbors[n];
				Item neighbor = Kernel.gi().getItems()[itemIndex];
				int neighborCode = neighbor.getItemCode();
				
								
				int i = testUser.getItemIndex(neighborCode);
				if (i != -1) {
					double similarity = similarities[itemIndex];
					double sim = (similarity - this.minSim) / (this.maxSim - this.minSim);
					
					double rating = testUser.getRatings()[i];
					
					predictions[testItemIndex] += sim * rating;
					sumSimilarities += sim;
				}
			}
			
			predictions[testItemIndex] = (sumSimilarities == 0) 
				? Double.NaN 
				: predictions[testItemIndex] / sumSimilarities;
		}

		testUser.setPredictions(predictions);
	}

	@Override
	public void afterRun() { }
}
