package cf4j.knn.userToUser.similarities;

import cf4j.TestUser;
import cf4j.User;

/**
 * Implements traditional Cosine as CF similarity metric.
 * 
 * @author Fernando Ortega
 */
public class MetricCosine extends UsersSimilarities {

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
				num += activeUser.getRatings()[i] * targetUser.getRatings()[j];
				denActive += activeUser.getRatings()[i] * activeUser.getRatings()[i];
				denTarget += targetUser.getRatings()[j] * targetUser.getRatings()[j];
				
				common++;
				i++; 
				j++;
			}	
		}

		// If there is not items in common, similarity does not exists
		if (common == 0) return Double.NEGATIVE_INFINITY;

		// Return similarity
		return num / (Math.sqrt(denActive) * Math.sqrt(denTarget));
	}
}
