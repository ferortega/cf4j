package cf4j.knn.itemToItem.neighbors;

import cf4j.Kernel;
import cf4j.TestItem;
import cf4j.TestItemsPartible;
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
public class Neighbors implements TestItemsPartible {

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
	public void run (int testItemIndex) {
		TestItem testItem = Kernel.gi().getTestItems()[testItemIndex];
		double [] similarities = testItem.getSimilarities();
		int [] neighbors = Methods.findTopN(similarities, this.k);
		testItem.setNeighbors(neighbors);
	}

	@Override
	public void afterRun() { }

}
