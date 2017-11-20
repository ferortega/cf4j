package cf4j.knn.userToUser.similarities;

import cf4j.Kernel;
import cf4j.TestUser;
import cf4j.User;

/**
 * Implements the following CF similarity metric: Bobadilla, J., Serradilla, F., 
 * &amp; Bernal, J. (2010). A new collaborative filtering metric that improves 
 * the behavior of Recommender Systems, Knowledge-Based Systems, 23 (6), 520-528.
 * 
 * @author Fernando Ortega
 */
public class MetricJMSD extends UsersSimilarities {

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

		int i = 0, j = 0, intersection = 0;
		double msd = 0d;
		
		while (i < activeUser.getNumberOfRatings() && j < targetUser.getNumberOfRatings()) {
			if (activeUser.getItems()[i] < targetUser.getItems()[j]) {
				i++;
			} else if (activeUser.getItems()[i] > targetUser.getItems()[j]) {
				j++;
			} else {
				double diff = (activeUser.getRatings()[i] - targetUser.getRatings()[j]) / this.maxDiff;
				msd += diff * diff;
				intersection++;
				i++; 
				j++;
			}	
		}

		// If there is not items in common, similarity does not exists
		if (intersection == 0) return Double.NEGATIVE_INFINITY;
		
		// Return similarity
		double union = activeUser.getNumberOfRatings() + targetUser.getNumberOfRatings() - intersection;
		double jaccard = intersection / union;
		return jaccard * (1d - (msd / intersection));
	}
}
