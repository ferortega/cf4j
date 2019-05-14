package cf4j.algorithms.knn.itemToItem.similarities;

import cf4j.data.DataModel;
import cf4j.data.Item;
import cf4j.data.TestItem;

/**
 * This class Implements Jaccard Index as CF similarity metric for the items.
 * 
 * @author Fernando Ortega
 */
public class Jaccard extends ItemSimilarities{

	public Jaccard(DataModel dataModel) {
		super(dataModel);
	}

	@Override
	public double similarity (TestItem activeItem, Item targetItem) {

		int u = 0, v = 0, common = 0;
		while (u < activeItem.getNumberOfRatings() && v < targetItem.getNumberOfRatings()) {
			if (activeItem.getUsers().get(u).compareTo(targetItem.getUsers().get(v)) < 0) {
				u++;
			} else if (activeItem.getUsers().get(u).compareTo(targetItem.getUsers().get(v)) > 0) {
				v++;
			} else {
				common++;
				u++; 
				v++;
			}	
		}

		// If there is not ratings in common, similarity does not exists
		if (common == 0) return Double.NEGATIVE_INFINITY;

		// Return similarity
		return (double) common / (double) (activeItem.getNumberOfRatings() + targetItem.getNumberOfRatings() - common);
	}
}
