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
 * <p>This class uses standard average as method to combine the test user neighbors' ratings.</p>
 * 
 * @author Fernando Ortega
 */
public class Mean implements TestUsersPartible {

	@Override
	public void beforeRun() { }

	@Override
	public void run (int testUserIndex) {

		TestUser testUser = Kernel.gi().getTestUsers()[testUserIndex];
		
		int [] neighbors = testUser.getNeighbors();
		
		int numRatings = testUser.getNumberOfTestRatings();
		double [] predictions = new double [numRatings];
		
		for (int testItemIndex = 0; testItemIndex < numRatings; testItemIndex++) {
			
			int itemCode = testUser.getTestItems()[testItemIndex];
			int count = 0;
			
			for (int n = 0; n <neighbors.length; n++) {
				if (neighbors[n] == -1) break; // Neighbors array are filled with -1 when no more neighbors exists
				
				int userIndex = neighbors[n];
				User neighbor = Kernel.gi().getUsers()[userIndex];
				
				int i = neighbor.getItemIndex(itemCode);
				if (i != -1) {
					predictions[testItemIndex] += neighbor.getRatings()[i];
					count++;
				}
			}
			predictions[testItemIndex] = (count == 0) ? Double.NaN : predictions[testItemIndex] / count;
		}

		testUser.setPredictions(predictions);
	}

	@Override
	public void afterRun() { }
}
