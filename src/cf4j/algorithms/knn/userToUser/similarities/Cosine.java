package cf4j.algorithms.knn.userToUser.similarities;

import cf4j.data.DataModel;
import cf4j.data.TestUser;
import cf4j.data.User;

/**
 * Implements traditional Cosine as CF similarity metric.
 * 
 * @author Fernando Ortega
 */
public class Cosine extends UserSimilarities {

	public Cosine(DataModel dataModel) {
		super(dataModel);
	}

	@Override
	public double similarity (TestUser activeUser, User targetUser) {		

		int i = 0, j = 0, common = 0; 
		double num = 0d, denActive = 0d, denTarget = 0d;
		
		while (i < activeUser.getNumberOfRatings() && j < targetUser.getNumberOfRatings()) {
			if (activeUser.getItemAt(i).compareTo(targetUser.getItemAt(j)) < 0) {
				i++;
			} else if (activeUser.getItemAt(i).compareTo(targetUser.getItemAt(j)) > 0) {
				j++;
			} else {
				num += activeUser.getRatingAt(i) * targetUser.getRatingAt(j);
				denActive += activeUser.getRatingAt(i) * activeUser.getRatingAt(i);
				denTarget += targetUser.getRatingAt(j) * targetUser.getRatingAt(j);
				
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
