package cf4j.knn.userToUser.similarities;

import cf4j.TestUser;
import cf4j.User;

/**
 * Implements traditional Jaccard Index as CF similarity metric.
 * 
 * @author Fernando Ortega
 */
public class MetricJaccard extends UsersSimilarities {

	@Override
	public double similarity (TestUser activeUser, User targetUser) {		
		
		int i = 0, j = 0, common = 0;
		while (i < activeUser.getNumberOfRatings() && j < targetUser.getNumberOfRatings()) {
			if (activeUser.getItems()[i] < targetUser.getItems()[j]) {
				i++;
			} else if (activeUser.getItems()[i] > targetUser.getItems()[j]) {
				j++;
			} else {
				common++;
				i++;
				j++;
			}	
		}
		
		// If there is not items in common, similarity does not exists
		if (common == 0) return Double.NEGATIVE_INFINITY;
			
		// Return similarity
		return (double) common / (double) (activeUser.getNumberOfRatings() + targetUser.getNumberOfRatings() - common);
	}
}
