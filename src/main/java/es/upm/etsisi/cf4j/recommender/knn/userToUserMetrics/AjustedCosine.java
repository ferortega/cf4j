package es.upm.etsisi.cf4j.recommender.knn.userToUserMetrics;


import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.Item;
import es.upm.etsisi.cf4j.data.User;

/**
 * Implements traditional Adjusted Cosine as CF similarity metric.
 * 
 * @author Fernando Ortega
 */
public class AjustedCosine extends UserToUserMetric {

	public AjustedCosine(DataModel datamodel, double[][] similarities) {
		super(datamodel, similarities);
	}

	@Override
	public double similarity(User user, User otherUser) {
		
		int i = 0, j = 0, common = 0; 
		double num = 0d, denActive = 0d, denTarget = 0d;
		
		while (i < user.getNumberOfRatings() && j < otherUser.getNumberOfRatings()) {
			if (user.getItem(i) < otherUser.getItem(j)) {
				i++;
			} else if (user.getItem(i) > otherUser.getItem(j)) {
				j++;
			} else {
				int itemIndex = user.getItem(i);
				Item item = super.datamodel.getItemAt(itemIndex);
				double avg = item.getAverageRating();
				
				double fa = user.getRatingAt(i) - avg;
				double ft = otherUser.getRatingAt(j) - avg;
				
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
