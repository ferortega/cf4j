package cf4j.knn.userToUser.similarities;

import cf4j.TestUser;
import cf4j.User;

/**
 * Implements traditional Pearson Correlation as CF similarity metric.
 * 
 * @author Fernando Ortega
 */
public class MetricCorrelation extends UsersSimilarities {

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
				double fa = activeUser.getRatings()[i] - activeUser.getRatingAverage();
				double ft = targetUser.getRatings()[j] - targetUser.getRatingAverage();
				
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
