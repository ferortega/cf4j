package cf4j.knn.userToUser.similarities;

import java.util.ArrayList;
import java.util.Collections;

import cf4j.Kernel;
import cf4j.TestUser;
import cf4j.User;

/**
 * Implements traditional Pearson Correlation Constrained as CF similarity metric.
 * 
 * @author Fernando Ortega
 */
public class MetricCorrelationConstrained extends UsersSimilarities {

	/**
	 * Median of the ratings of the dataset
	 */
	private double median;
	
	/**
	 * Constructor of the similarity metric
	 * @param median Median of the ratings of the dataset
	 */
	public MetricCorrelationConstrained (double median) {
		this.median = median;
	}
	
	/**
	 * Constructor of the similarity metric. Median is computed automatically (high CPU cost).
	 */
	public MetricCorrelationConstrained () {
		ArrayList <Double> ratings = new ArrayList <Double> ();
		for (User user : Kernel.gi().getUsers()) {
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
	public double similarity (TestUser activeUser, User targetUser) {		

		int i = 0, j = 0, common = 0; 
		double num = 0d, denActive = 0d, denTarget = 0d;
		
		while (i < activeUser.getNumberOfRatings() && j < targetUser.getNumberOfRatings()) {
			if (activeUser.getItems()[i] < targetUser.getItems()[j]) {
				i++;
			} else if (activeUser.getItems()[i] > targetUser.getItems()[j]) {
				j++;
			} else {
				double fa = activeUser.getRatings()[i] - this.median;
				double ft = targetUser.getRatings()[j] - this.median;
				
				num += fa * ft;
				denActive += fa * fa;
				denTarget += ft * ft;
				
				common++;
				i++;
				j++;
			}	
		}

		// If there is not items in common, similarity does not exists
		if (common == 0) return Double.NEGATIVE_INFINITY;

		// Denominator can not be zero
		if (denActive == 0 || denTarget == 0) return Double.NEGATIVE_INFINITY;

		// Return similarity
		return num / Math.sqrt(denActive * denTarget);
	}
}
