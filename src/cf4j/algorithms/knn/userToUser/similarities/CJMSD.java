package cf4j.algorithms.knn.userToUser.similarities;

import cf4j.data.DataModel;
import cf4j.data.TestUser;
import cf4j.data.User;

/**
 * Implements the following CF similarity metric: Bobadilla, J., Ortega, F., Hernando, A., 
 * &amp; Arroyo, A. (2012). A Balanced Memory-Based Collaborative Filtering Similarity 
 * Measure, International Journal of Intelligent Systems, 27, 939-946.
 * 
 * @author Fernando Ortega
 */
public class CJMSD extends UserSimilarities {

	/**
	 * Maximum difference between the ratings
	 */
	private double maxDiff;

	public CJMSD(DataModel dataModel) {
		super(dataModel);
	}

	@Override
	public void beforeRun () {
		super.beforeRun();
		this.maxDiff = this.dataModel.getStoredData().getDouble(DataModel.MAXRATING_KEY) - this.dataModel.getStoredData().getDouble(DataModel.MINRATING_KEY);
	}

	@Override
	public double similarity (TestUser activeUser, User targetUser) {		

		int i = 0, j = 0, common = 0; 
		double msd = 0d;
		
		while (i < activeUser.getNumberOfRatings() && j < targetUser.getNumberOfRatings()) {
			if (activeUser.getItems().get(i).compareTo(targetUser.getItems().get(j))<0) {
				i++;
			} else if (activeUser.getItems().get(i).compareTo(targetUser.getItems().get(j))>0) {
				j++;
			} else {
				double diff = (activeUser.getRatings().get(i) - targetUser.getRatings().get(j)) / this.maxDiff;
				msd += diff * diff;
				common++;
				i++; j++;
			}	
		}

		// If there is not items in common, similarity does not exists
		if (common == 0) return Double.NEGATIVE_INFINITY;

		// Return similarity
		double jaccard = (double) common / (double) (activeUser.getNumberOfRatings() + targetUser.getNumberOfRatings() - common);
		double coverage = (double) (targetUser.getNumberOfRatings() - common) / (double) this.dataModel.getNumberOfItems();
		return coverage * jaccard * (1d - (msd / common));
	}
}
