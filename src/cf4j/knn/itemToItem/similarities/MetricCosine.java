package cf4j.knn.itemToItem.similarities;

import cf4j.Item;
import cf4j.TestItem;

/**
 * Implements Cosine as CF similarity metric for the items.
 * 
 * @author Fernando Ortega
 */
public class MetricCosine extends ItemsSimilarities{

	@Override
	public double similarity (TestItem activeItem, Item targetItem) {

		int u = 0, v = 0, common = 0; 
		double num = 0d, denActive = 0d, denTarget = 0d;
		
		while (u < activeItem.getNumberOfRatings() && v < targetItem.getNumberOfRatings()) {
			if (activeItem.getUsers()[u] < targetItem.getUsers()[v]) {
				u++;
			} else if (activeItem.getUsers()[u] > targetItem.getUsers()[v]) {
				v++;
			} else {
				num += activeItem.getRatings()[u] * targetItem.getRatings()[v];
				denActive += activeItem.getRatings()[u] * activeItem.getRatings()[u];
				denTarget += targetItem.getRatings()[v] * targetItem.getRatings()[v];
				
				common++;
				u++; 
				v++;
			}	
		}

		// If there is not ratings in common, similarity does not exists
		if (common == 0) return Double.NEGATIVE_INFINITY;

		// Return similarity
		return num / (Math.sqrt(denActive) * Math.sqrt(denTarget));
	}
}
