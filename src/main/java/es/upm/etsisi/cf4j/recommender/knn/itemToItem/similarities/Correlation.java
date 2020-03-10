package cf4j.algorithms.knn.itemToItem.similarities;

import cf4j.data.DataModel;
import cf4j.data.Item;
import cf4j.data.TestItem;

/**
 * This class Implements Pearson Correlation as CF similarity metric for the items.
 * 
 * @author Fernando Ortega
 */
public class Correlation extends ItemSimilarities{

	public Correlation(DataModel dataModel) {
		super(dataModel);
	}

	@Override
	public double similarity (TestItem activeItem, Item targetItem) {

		int u = 0, v = 0, common = 0; 
		double num = 0d, denActive = 0d, denTarget = 0d;
		
		while (u < activeItem.getNumberOfRatings() && v < targetItem.getNumberOfRatings()) {
			if ( activeItem.getUserAt(u).compareTo(targetItem.getUserAt(v)) < 0 ){
				u++;
			} else if (activeItem.getUserAt(u).compareTo(targetItem.getUserAt(v)) > 0) {
				v++;
			} else {
				double fa = activeItem.getRatingAt(u) - activeItem.getDataBank().getDouble(Item.AVERAGERATING_KEY);
				double ft = targetItem.getRatingAt(v) - targetItem.getDataBank().getDouble(Item.AVERAGERATING_KEY);
				
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
