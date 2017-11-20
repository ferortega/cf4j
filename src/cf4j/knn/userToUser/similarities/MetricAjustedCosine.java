package cf4j.knn.userToUser.similarities;

import cf4j.Item;
import cf4j.Kernel;
import cf4j.TestUser;
import cf4j.User;

/**
 * Implements traditional Adjusted Cosine as CF similarity metric.
 * 
 * @author Fernando Ortega
 */
public class MetricAjustedCosine extends UsersSimilarities {

	@Override
	public double similarity (TestUser activeUser, User targetUser) {	
		
		int i = 0, j = 0, common = 0; 
		double num = 0d, denActive = 0d, denTarget = 0d;
		
		while (i < activeUser.getNumberOfRatings() && j < targetUser.getNumberOfRatings()) {
			if (activeUser.getItems()[i] < targetUser.getItems()[j]) {
				i++;
			} else if (activeUser.getItems()[i] > targetUser.getItems()[j]) {
				j++;
			} else {
				int itemCode = activeUser.getItems()[i];
				Item item = Kernel.gi().getItemByCode(itemCode);
				double avg = item.getRatingAverage();
				
				double fa = activeUser.getRatings()[i] - avg;
				double ft = targetUser.getRatings()[j] - avg;
				
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
