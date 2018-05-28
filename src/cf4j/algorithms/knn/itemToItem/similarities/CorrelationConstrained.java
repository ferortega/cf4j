package cf4j.algorithms.knn.itemToItem.similarities;

import java.util.ArrayList;
import java.util.Collections;

import cf4j.data.Item;
import cf4j.data.DataModel;
import cf4j.data.TestItem;
import cf4j.data.User;

/**
 * This class implements the Constrained Correlation as CF similarity metric for items.
 * 
 * @author Fernando Ortega
 */
public class CorrelationConstrained extends ItemSimilarities{

	/**
	 * Median of the ratings of the dataset
	 */
	private double median;
	
	/**
	 * Constructor of the similarity metric
	 * @param median Median of the ratings of the dataset
	 */
	public CorrelationConstrained (double median) {
		this.median = median;
	}
	
	/**
	 * Constructor of the similarity metric. Median is computed automatically (high CPU cost).
	 */
	public CorrelationConstrained () {
		ArrayList <Double> ratings = new ArrayList <Double> ();
		for (User user : DataModel.gi().getUsers()) {
			for (double rating : user.getRatings()) {
				ratings.add(rating);
			}
		}
		
		Collections.sort(ratings);
		
		int p0 = (int) Math.floor(ratings.size() / 2 + 0.5);
		int p1 = (int) Math.ceil(ratings.size() / 2 + 0.5);
		
		this.median = (ratings.get(p0) + ratings.get(p1)) / 2.0;
	}
	
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
				double fa = activeItem.getRatings()[u] - this.median;
				double ft = targetItem.getRatings()[v] - this.median;
				
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
