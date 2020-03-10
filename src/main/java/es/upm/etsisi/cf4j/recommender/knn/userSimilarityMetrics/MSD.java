package cf4j.algorithms.knn.userSimilarityMetrics;

import cf4j.data.DataModel;
import cf4j.data.TestUser;
import cf4j.data.User;

/**
 * Implements traditional MSD as CF similarity metric. The returned value is 1 - MSD.
 * 
 * @author Fernando Ortega
 */
public class MSD extends UserSimilarities {

	/**
	 * Maximum difference between the ratings
	 */
	private double maxDiff;

	public MSD(DataModel datamodel, double[][] similarities) {
		super(datamodel, similarities);
		this.maxDiff = super.datamodel.getMaxRating() - super.datamodel.getMinRating();
	}
	
	@Override
	public double similarity(TestUser testUser, User otherUser) {

		int i = 0, j = 0, common = 0; 
		double msd = 0d;
		
		while (i < testUser.getNumberOfRatings() && j < otherUser.getNumberOfRatings()) {
			if (testUser.getItemAt(i) < otherUser.getItemAt(j)) {
				i++;
			} else if (testUser.getItemAt(i) > otherUser.getItemAt(j)) {
				j++;
			} else {
				double diff = (testUser.getRatingAt(i) - otherUser.getRatingAt(j)) / this.maxDiff;
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
