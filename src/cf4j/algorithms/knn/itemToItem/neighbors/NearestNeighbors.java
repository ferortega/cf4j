package cf4j.algorithms.knn.itemToItem.neighbors;

import cf4j.data.TestItem;
import cf4j.utils.Methods;

/**
 * <p>This class calculates the neighbors of each test item. It saves in every test item map 
 * the key <b>"neighbors"</b> which references an array of integers containing the indexes of 
 * the items that are neighbors of the test item.</p>
 * 
 * <p>Similarities between test items must be computed before the usage of this class.</p>
 * 
 * @author Fernando Ortega
 */
public class NearestNeighbors extends ItemNeighbors {

	/**
	 * Class constructor
	 * @param k Number of neighbors to calculate
	 */
	public NearestNeighbors (int k) {
		super(k);
	}
	
	@Override
	public int [] neighbors (TestItem testItem) {
		double [] similarities = testItem.getSimilarities();
		int [] neighbors = Methods.findTopN(similarities, super.k);
		return neighbors;
	}
}
