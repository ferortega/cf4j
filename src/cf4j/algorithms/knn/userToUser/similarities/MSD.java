package cf4j.algorithms.knn.userToUser.similarities;

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

	public MSD(DataModel dataModel) {
		super(dataModel);
	}

	@Override
	public void beforeRun () {		
		super.beforeRun();
		this.maxDiff = this.dataModel.getMaxRating() - this.dataModel.getMinRating();
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
