package cf4j.algorithms.knn.itemToItem.similarities;

import cf4j.data.DataModel;
import cf4j.data.Item;
import cf4j.data.TestItem;

/**
 * Implements Cosine as CF similarity metric for the items.
 * 
 * @author Fernando Ortega
 */
public class Cosine extends ItemSimilarities{

	public Cosine(DataModel dataModel) {
		super(dataModel);
	}

	@Override
	public double similarity (TestItem activeItem, Item targetItem) {

		int u = 0, v = 0, common = 0; 
		double num = 0d, denActive = 0d, denTarget = 0d;
		
		while (u < activeItem.getNumberOfRatings() && v < targetItem.getNumberOfRatings()) {
			if (activeItem.getUsers().get(u).compareTo(targetItem.getUsers().get(v)) < 0) {
				u++;
			} else if (activeItem.getUsers().get(u).compareTo(targetItem.getUsers().get(v)) > 0) {
				v++;
			} else {
				num += activeItem.getRatings().get(u) * targetItem.getRatings().get(v);
				denActive += activeItem.getRatings().get(u) * activeItem.getRatings().get(u);
				denTarget += targetItem.getRatings().get(v) * targetItem.getRatings().get(v);
				
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
