package es.upm.etsisi.cf4j.recommender.knn.itemToItemMetrics;


import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.Item;

/**
 * Implements Cosine as CF similarity metric for the items.
 * 
 * @author Fernando Ortega
 */
public class Cosine extends ItemToItemMetric {

	public Cosine(DataModel datamodel, double[][] similarities) {
		super(datamodel, similarities);
	}

	@Override
	public double similarity(Item item, Item otherItem) {
		int u = 0, v = 0, common = 0; 
		double num = 0d, denActive = 0d, denTarget = 0d;

		while (u < item.getNumberOfRatings() && v < otherItem.getNumberOfRatings()) {
			if (item.getUser(u) < otherItem.getUser(v)) {
				u++;
			} else if (item.getUser(u) > otherItem.getUser(v)) {
				v++;
			} else {
				num += item.getRating(u) * otherItem.getRating(v);
				denActive += item.getRating(u) * item.getRating(u);
				denTarget += otherItem.getRating(v) * otherItem.getRating(v);
				
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
