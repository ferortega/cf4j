package cf4j.algorithms.knn.userToUser.neighbors;

import cf4j.data.DataModel;
import cf4j.data.TestUser;
import cf4j.utils.Methods;

/**
 * <p>Set the most similar training users as user neighbors.</p>
 * 
 * <p>Similarities between test users and training users must be computed before 
 * the usage of this class.</p>
 * 
 * @author Fernando Ortega
 */
public class NearestNeighbors extends UserNeighbors {

	/**
	 * Class constructor
	 * @param k Number of neighbors to calculate
	 */
	public NearestNeighbors(DataModel dataModel, int k) {
		super(dataModel, k);
	}

	@Override
	public Integer [] neighbors (TestUser testUser) {
		Double [] similarities = testUser.getStoredData().getDoubleArray(TestUser.SIMILARITIES_KEY);
		Integer [] neighbors = Methods.findTopN(similarities, super.k);
		return neighbors;
	}
}
