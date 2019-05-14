package cf4j.algorithms.knn.itemToItem.neighbors;

import cf4j.data.DataModel;
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
	public NearestNeighbors (DataModel dataModel, int k) {
		super(dataModel, k);
	}
	
	@Override
	public Integer [] neighbors (TestItem testItem) {
		Double [] similarities = testItem.getStoredData().getDoubleArray(TestItem.SIMILARITIES_KEY);
		Integer [] neighbors = Methods.findTopN(similarities, super.k);
		return neighbors;
	}
}
