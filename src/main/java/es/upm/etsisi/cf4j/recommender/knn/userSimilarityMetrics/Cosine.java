package cf4j.algorithms.knn.userSimilarityMetrics;

import cf4j.data.DataModel;
import cf4j.data.TestUser;
import cf4j.data.User;

/**
 * Implements traditional Cosine as CF similarity metric.
 * 
 * @author Fernando Ortega
 */
public class Cosine extends UserSimilarities {

	public Cosine(DataModel datamodel, double[][] similarities) {
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
				num += testUser.getRatingAt(i) * otherUser.getRatingAt(j);
				denActive += testUser.getRatingAt(i) * testUser.getRatingAt(i);
				denTarget += otherUser.getRatingAt(j) * otherUser.getRatingAt(j);
				
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
