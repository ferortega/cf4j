package es.upm.etsisi.cf4j.recommender.knn.itemToItemMetrics;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.Item;

/**
 * This class Implements Jaccard Index as CF similarity metric for the items.
 * 
 * @author Fernando Ortega
 */
public class Jaccard extends ItemToItemMetric {

	public Jaccard(DataModel datamodel, double[][] similarities) {
		super(datamodel, similarities);
	}

	@Override
	public double similarity(Item item, Item otherItem) {
		int u = 0, v = 0, common = 0;
		while (u < item.getNumberOfRatings() && v < otherItem.getNumberOfRatings()) {
			if (item.getUserAt(u) < otherItem.getUserAt(v)) {
				u++;
			} else if (item.getUserAt(u) > otherItem.getUserAt(v)) {
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
		return (double) common / (double) (item.getNumberOfRatings() + otherItem.getNumberOfRatings() - common);
	}
}
