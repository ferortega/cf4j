package es.upm.etsisi.cf4j.recommender.knn.userToUserMetrics;


import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.User;

/**
 * Implements the following CF similarity metric: Bobadilla, J., Serradilla, F., 
 * &amp; Bernal, J. (2010). A new collaborative filtering metric that improves 
 * the behavior of Recommender Systems, Knowledge-Based Systems, 23 (6), 520-528.
 * 
 * @author Fernando Ortega
 */
public class JMSD extends UserToUserMetric {

	/**
	 * Maximum difference between the ratings
	 */
	private double maxDiff;

	public JMSD(DataModel datamodel, double[][] similarities) {
		super(datamodel, similarities);
		this.maxDiff = super.datamodel.getMaxRating() - super.datamodel.getMinRating();
	}
	
	@Override
	public double similarity(User user, User otherUser) {

		int i = 0, j = 0, intersection = 0;
		double msd = 0d;
		
		while (i < user.getNumberOfRatings() && j < otherUser.getNumberOfRatings()) {
			if (user.getItem(i) < otherUser.getItem(j)) {
				i++;
			} else if (user.getItem(i) > otherUser.getItem(j)) {
				j++;
			} else {
				double diff = (user.getRatingAt(i) - otherUser.getRatingAt(j)) / this.maxDiff;
				msd += diff * diff;
				intersection++;
				i++; 
				j++;
			}	
		}

		// If there is not items in common, similarity does not exists
		if (intersection == 0) return Double.NEGATIVE_INFINITY;
		
		// Return similarity
		double union = user.getNumberOfRatings() + otherUser.getNumberOfRatings() - intersection;
		double jaccard = intersection / union;
		return jaccard * (1d - (msd / intersection));
	}
}
