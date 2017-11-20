package cf4j.knn.userToUser.similarities;

import cf4j.Kernel;
import cf4j.TestUser;
import cf4j.User;

/**
 * Implements the following CF similarity metric: Bobadilla, J., Ortega, F., Hernando, A., 
 * &amp; Arroyo, A. (2012). A Balanced Memory-Based Collaborative Filtering Similarity 
 * Measure, International Journal of Intelligent Systems, 27, 939-946.
 * 
 * @author Fernando Ortega
 */
public class MetricCJMSD extends UsersSimilarities {

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
				i++; j++;
			}	
		}

		// If there is not items in common, similarity does not exists
		if (common == 0) return Double.NEGATIVE_INFINITY;

		// Return similarity
		double jaccard = (double) common / (double) (activeUser.getNumberOfRatings() + targetUser.getNumberOfRatings() - common);
		double coverage = (double) (targetUser.getNumberOfRatings() - common) / (double) Kernel.gi().getNumberOfItems();
		return coverage * jaccard * (1d - (msd / common));
	}
}
