package cf4j.knn.userToUser.similarities;

import cf4j.Kernel;
import cf4j.TestUser;
import cf4j.User;

/**
 * Implements traditional MSD as CF similarity metric. The returned value is 1 - MSD.
 * 
 * @author Fernando Ortega
 */
public class MetricMSD extends UsersSimilarities {

	/**
	 * Maximum difference between the ratings
	 */
	private double maxDiff;
	
	@Override
	public void beforeRun () {		
		super.beforeRun();
		this.maxDiff = Kernel.gi().getMaxRating() - Kernel.gi().getMinRating();
	}
	
	@Override
	public double similarity (TestUser activeUser, User targetUser) {		

		int i = 0, j = 0, common = 0; 
		double msd = 0d;
		
		while (i < activeUser.getNumberOfRatings() && j < targetUser.getNumberOfRatings()) {
			if (activeUser.getItems()[i] < targetUser.getItems()[j]) {
				i++;
			} else if (activeUser.getItems()[i] > targetUser.getItems()[j]) {
				j++;
			} else {
				double diff = (activeUser.getRatings()[i] - targetUser.getRatings()[j]) / this.maxDiff;
				msd += diff * diff;				
				
				common++;
				i++; 
				j++;
			}	
		}

		// If there is not items in common, similarity does not exists
		if (common == 0) return Double.NEGATIVE_INFINITY;

		// Return similarity
		return 1d - (msd / common);
	}
}
