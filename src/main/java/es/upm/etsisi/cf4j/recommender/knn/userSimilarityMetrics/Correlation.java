package cf4j.algorithms.knn.userSimilarityMetrics;

import cf4j.data.DataModel;
import cf4j.data.TestUser;
import cf4j.data.User;

/**
 * Implements traditional Pearson Correlation as CF similarity metric.
 * 
 * @author Fernando Ortega
 */
public class Correlation extends UserSimilarities {

	public Correlation(DataModel datamodel, double[][] similarities) {
		super(datamodel, similarities);
	}

	@Override
	public double similarity(TestUser testUser, User otherUser) {

		int i = 0, j = 0, common = 0; 
		double num = 0d, denActive = 0d, denTarget = 0d;
		
		while (i < testUser.getNumberOfRatings() && j < otherUser.getNumberOfRatings()) {
			if (testUser.getItemAt(i) < otherUser.getItemAt(j)) {
				i++;
			} else if (testUser.getItemAt(i) > otherUser.getItemAt(j)) {
				j++;
			} else {
				double t = testUser.getRatingAt(i) - testUser.getRatingAverage();
				double o = otherUser.getRatingAt(j) - otherUser.getRatingAverage();
				
				num += t * o;
				denActive += t * t;
				denTarget += o * o;
				
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
		double correlation = num / Math.sqrt(denActive * denTarget);
		return (correlation + 1.0) / 2.0;
	}
}
