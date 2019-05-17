package cf4j.algorithms.knn.userToUser.similarities;

import cf4j.data.DataModel;
import cf4j.data.TestUser;
import cf4j.data.User;

/**
 * Implements the following CF similarity metric: Bobadilla, J., Serradilla, F., 
 * &amp; Bernal, J. (2010). A new collaborative filtering metric that improves 
 * the behavior of Recommender Systems, Knowledge-Based Systems, 23 (6), 520-528.
 * 
 * @author Fernando Ortega
 */
public class JMSD extends UserSimilarities {

	/**
	 * Maximum difference between the ratings
	 */
	private double maxDiff;

	public JMSD(DataModel dataModel) {
		super(dataModel);
	}

	@Override
	public void beforeRun () {
		super.beforeRun();
		this.maxDiff = dataModel.getDataBank().getDouble(DataModel.MAXRATING_KEY) - dataModel.getDataBank().getDouble(DataModel.MINRATING_KEY);
	}
	
	@Override
	public double similarity (TestUser activeUser, User targetUser) {		

		int i = 0, j = 0, intersection = 0;
		double msd = 0d;
		
		while (i < activeUser.getNumberOfRatings() && j < targetUser.getNumberOfRatings()) {
			if (activeUser.getItems().get(i).compareTo( targetUser.getItems().get(j)) < 0) { //TODO: Check this. Could be reversed
				i++;
			} else if (activeUser.getItems().get(i).compareTo( targetUser.getItems().get(j)) > 0) { //TODO: Check this. Could be reversed
				j++;
			} else {
				double diff = (activeUser.getRatings().get(i) - targetUser.getRatings().get(j)) / this.maxDiff;
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
