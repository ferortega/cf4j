package cf4j.knn.itemToItem.similarities;

import cf4j.Item;
import cf4j.TestItem;

/**
 * This class Implements Pearson Correlation as CF similarity metric for the items.
 * 
 * @author Fernando Ortega
 */
public class MetricCorrelation extends ItemsSimilarities{

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
				double fa = activeItem.getRatings()[u] - activeItem.getRatingAverage();
				double ft = targetItem.getRatings()[v] - targetItem.getRatingAverage();
				
				num += fa * ft;
				denActive += fa * fa;
				denTarget += ft * ft;
				
				common++;
				u++;
				v++;
			}	
		}

		// If there is not ratings in common, similarity does not exists
		if (common == 0) return Double.NEGATIVE_INFINITY;

		// Denominator can not be zero
		if (denActive == 0 || denTarget == 0) return Double.NEGATIVE_INFINITY;

		// Return similarity
		return num / Math.sqrt(denActive * denTarget);
	}
}
