package cf4j.algorithms.knn.userToUser.neighbors;

import cf4j.data.DataModel;
import cf4j.data.TestUser;
import cf4j.process.TestUsersPartible;

/**
 * <p>This abstracts class calculates the neighbors of each test user. If you want to compute
 * your own algorithm to compute user neighbors, you must extend this class and redefine 
 * neighbors method.</p>
 * 
 * <p>Neighbors are store at every test user map with the key <b>"neighbors"</b>. This key
 * points to an array of integers containing the indexes of the users that are neighbors of 
 * the test user.</p>
 * 
 * @author Fernando Ortega
 */
public abstract class UserNeighbors implements TestUsersPartible {

	/**
	 * Number of neighbors to be calculated
	 */
	protected int k;
	
	/**
	 * Class constructor
	 * @param k Number of neighbors to calculate
	 */
	public UserNeighbors (int k) {
		this.k = k;
	}
	
	@Override
	public void beforeRun() { }

	@Override
	public void run (int testUserIndex) {
		TestUser testUser = DataModel.getInstance().getTestUserByIndex(testUserIndex);
		int [] neighbors = this.neighbors(testUser);
		testUser.setNeighbors(neighbors);
	}

	@Override
	public void afterRun() { }
	
	/**
	 * Computes neighbors of a testUser
	 * @param testUser Active user
	 * @return Array of integer with indexes of the training users that are neighbors
	 *     of the active user. Fill with -1 it there is not more neighbors.  
	 */
	public abstract int [] neighbors (TestUser testUser);
}
