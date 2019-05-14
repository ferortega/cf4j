package cf4j.algorithms.knn.userToUser.similarities;

import cf4j.data.DataModel;
import cf4j.data.TestUser;
import cf4j.data.User;

/**
 * Implements traditional Sepearman Rank as CF similarity metric.
 * 
 * @author Fernando Ortega
 */
public class SpearmanRank extends UserSimilarities{

	public SpearmanRank(DataModel dataModel) {
		super(dataModel);
	}

	@Override
	public double similarity (TestUser activeUser, User targetUser) {		

		int i = 0, j = 0, common = 0; 
		double num = 0d;
		
		while (i < activeUser.getNumberOfRatings() && j < targetUser.getNumberOfRatings()) {
			if (activeUser.getItems().get(i).compareTo(targetUser.getItems().get(j)) < 0) {
				i++;
			} else if (activeUser.getItems().get(i).compareTo(targetUser.getItems().get(j)) > 0) {
				j++;
			} else {
				double diff = activeUser.getRatings().get(i) - targetUser.getRatings().get(j);
				num += diff * diff;
				common++;
				i++; 
				j++;
			}	
		}

		// If there is not items in common, similarity does not exists
		if (common == 0) return Double.NEGATIVE_INFINITY;

		// Return similarity
		return 1d - ((6d * num) / (common * ((common * common) - 1d)));
	}
}
