package cf4j.algorithms.knn.userSimilarityMetrics;

import cf4j.data.DataModel;
import cf4j.data.TestUser;
import cf4j.data.User;

/**
 * Implements traditional Jaccard Index as CF similarity metric.
 * 
 * @author Fernando Ortega
 */
public class Jaccard extends UserSimilarities {


	public Jaccard(DataModel datamodel, double[][] similarities) {
		super(datamodel, similarities);
	}

	@Override
	public double similarity(TestUser testUser, User otherUser) {
		
		int i = 0, j = 0, common = 0;
		while (i < testUser.getNumberOfRatings() && j < otherUser.getNumberOfRatings()) {
			if (testUser.getItemAt(i) < otherUser.getItemAt(j)) {
				i++;
			} else if (testUser.getItemAt(i) > otherUser.getItemAt(j)) {
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
		return (double) common / (double) (testUser.getNumberOfRatings() + otherUser.getNumberOfRatings() - common);
	}
}
