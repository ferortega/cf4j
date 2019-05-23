package cf4j.algorithms.knn.userToUser.similarities;

import cf4j.data.DataModel;
import cf4j.data.TestUser;
import cf4j.data.User;

/**
 * Implements traditional Pearson Correlation as CF similarity metric.
 * 
 * @author Fernando Ortega
 */
public class Correlation extends UserSimilarities {

	public Correlation(DataModel dataModel) {
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
				double avgFa = (activeUser.getNumberOfRatings()>0)?activeUser.getDataBank().getDouble(User.AVERAGERATING_KEY):0;
				double avgFt = (targetUser.getNumberOfRatings()>0)?targetUser.getDataBank().getDouble(User.AVERAGERATING_KEY):0;
				double fa = activeUser.getRatingAt(i) - avgFa;
				double ft = targetUser.getRatingAt(j) - avgFt;
				
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
