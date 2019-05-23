package cf4j.algorithms.knn.userToUser.similarities;

import cf4j.data.DataModel;
import cf4j.data.TestUser;
import cf4j.data.User;

/**
 * Implements traditional Jaccard Index as CF similarity metric.
 * 
 * @author Fernando Ortega
 */
public class Jaccard extends UserSimilarities {

	public Jaccard(DataModel dataModel) {
		super(dataModel);
	}

	@Override
	public double similarity (TestUser activeUser, User targetUser) {		
		
		int i = 0, j = 0, common = 0;
		while (i < activeUser.getNumberOfRatings() && j < targetUser.getNumberOfRatings()) {
			if (activeUser.getItemAt(i).compareTo(targetUser.getItemAt(j))<0) {
				i++;
			} else if (activeUser.getItemAt(i).compareTo(targetUser.getItemAt(j))>0) {
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
