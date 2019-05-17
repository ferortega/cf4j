package cf4j.algorithms.knn.itemToItem.similarities;

import cf4j.data.Item;
import cf4j.data.DataModel;
import cf4j.data.TestItem;
import cf4j.data.User;

/**
 * Implements traditional Ajusted Cosine as CF similarity metric for the items.
 * 
 * @author Fernando Ortega
 */
public class AjustedCosine extends ItemSimilarities {

	public AjustedCosine(DataModel dataModel) {
		super(dataModel);
	}

	@Override
	public double similarity (TestItem activeItem, Item targetItem) {
		
		int u = 0, v = 0, common = 0; 
		double num = 0d, denActive = 0d, denTarget = 0d;
		
		while (u < activeItem.getNumberOfRatings() && v < targetItem.getNumberOfRatings()) {
			if (activeItem.getUserAt(u).compareTo(targetItem.getUserAt(v)) < 0) {
				u++;
			} else if (activeItem.getUserAt(u).compareTo(targetItem.getUserAt(v)) > 0) {
				v++;
			} else {
				String userCode = activeItem.getUserAt(u);
				User user = this.dataModel.getUser(userCode);
				double avg = user.getRatingAverage();
				
				double fa = activeItem.getRatingAt(u) - avg;
				double ft = targetItem.getRatingAt(v) - avg;
				
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
