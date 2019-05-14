package cf4j.algorithms.knn.itemToItem.similarities;

import cf4j.data.DataModel;
import cf4j.data.Item;
import cf4j.data.TestItem;

/**
 * Implements traditional Sepearman Rank as CF similarity metric for the items.
 * 
 * @author Fernando Ortega
 */
public class SpearmanRank extends ItemSimilarities{

	public SpearmanRank(DataModel dataModel) {
		super(dataModel);
	}

	@Override
	public double similarity (TestItem activeItem, Item targetItem) {

		int u = 0, v = 0, common = 0; 
		double num = 0d;
		
		while (u < activeItem.getNumberOfRatings() && v < targetItem.getNumberOfRatings()) {

			if (activeItem.getUsers().get(u).compareTo(targetItem.getUsers().get(v))<0) {
				u++;
			} else if (activeItem.getUsers().get(u).compareTo(targetItem.getUsers().get(v))>0) {
				v++;
			} else {
				double diff = activeItem.getRatings().get(u) - targetItem.getRatings().get(v);
				num += diff * diff;
				common++;
				u++; 
				v++;
			}	
		}

		// If there is not ratings in common, similarity does not exists
		if (common == 0) return Double.NEGATIVE_INFINITY;
		
		// Return similarity
		return 1d - ((6d * num) / (common * ((common * common) - 1d)));
	}
}
