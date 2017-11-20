package cf4j.knn.userToUser.aggregationApproaches;

import cf4j.Kernel;
import cf4j.TestUser;
import cf4j.TestUsersPartible;
import cf4j.User;

/**
 * <p>This class computes the prediction of the test users' test items. The results are 
 * saved in double array on the hashmap of each test user with the key "predictions". This 
 * array overlaps with the test items' array of the test users. For example, the prediction
 * retrieved with the method testUser.getPredictions()[i] is the prediction of the item
 * testUser.getTestItems()[i].</p>
 * 
 * <p>This class uses deviation from mean as method to combine the test user neighbors' 
 * ratings.</p>
 * 
 * @author Fernando Ortega
 */
public class DeviationFromMean implements TestUsersPartible {

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
		
		for (TestUser testUser : Kernel.gi().getTestUsers()) {
			for (double m : testUser.getSimilarities()) {
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

		int [] neighbors = testUser.getNeighbors();
		double [] similarities = testUser.getSimilarities();

		int numRatings = testUser.getNumberOfTestRatings();
		double [] predictions = new double [numRatings];
		
		for (int testItemIndex = 0; testItemIndex < numRatings; testItemIndex++) {
			
			int itemCode = testUser.getTestItems()[testItemIndex];
			double sumSimilarities = 0;
			
			for (int n = 0; n < neighbors.length; n++) {
				if (neighbors[n] == -1) break; // Neighbors array are filled with -1 when no more neighbors exists
				
				int userIndex = neighbors[n];
				User neighbor = Kernel.gi().getUsers()[userIndex];
				
				int i = neighbor.getItemIndex(itemCode);
				if (i != -1) {
					double similarity = similarities[userIndex];
					double sim = (similarity - this.minSim) / (this.maxSim - this.minSim);

					predictions[testItemIndex] += sim * (neighbor.getRatings()[i] - neighbor.getRatingAverage());
					sumSimilarities += sim;
				}
			}
			
			if (sumSimilarities == 0) {
				predictions[testItemIndex] = Double.NaN;
			} 
			else {
				double deviation = predictions[testItemIndex] / sumSimilarities;
				double prediction = testUser.getRatingAverage() + deviation;
				prediction = Math.min(prediction, Kernel.gi().getMaxRating());
				prediction = Math.max(prediction, Kernel.gi().getMinRating());

				predictions[testItemIndex] = prediction;
			}
		}
		
		testUser.setPredictions(predictions);
	}

	@Override
	public void afterRun() { }
}
