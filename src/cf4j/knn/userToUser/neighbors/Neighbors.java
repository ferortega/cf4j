package cf4j.knn.userToUser.neighbors;

import cf4j.Kernel;
import cf4j.TestUser;
import cf4j.TestUsersPartible;
import cf4j.utils.Methods;

/**
 * <p>This class calculates the neighbors of each test user. It saves in every test user map 
 * the key <b>"neighbors"</b> which references an array of integers containing the indexes of 
 * the users that are neighbors of the test user.</p>
 * 
 * <p>Similarities between test users must be computed before the usage of this class.</p>
 * 
 * @author Fernando Ortega
 */
public class Neighbors implements TestUsersPartible {

	/**
	 * Number of neighbors to be calculated
	 */
	int k;
	
	/**
	 * Class constructor
	 * @param k Number of neighbors to calculate
	 */
	public Neighbors (int k) {
		this.k = k;
	}
	
	@Override
	public void beforeRun() { }

	@Override
	public void run (int testUserIndex) {
		TestUser testUser = Kernel.getInstance().getTestUsers()[testUserIndex];
		double [] similarities = testUser.getSimilarities();
		int [] neighbors = Methods.findTopN(similarities, this.k);
		testUser.setNeighbors(neighbors);
	}

	@Override
	public void afterRun() { }

}
