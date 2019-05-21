package cf4j.algorithms.knn.userToUser.similarities;

import cf4j.data.Item;
import cf4j.data.DataModel;
import cf4j.data.TestUser;
import cf4j.data.User;

/**
 * Implements traditional Adjusted Cosine as CF similarity metric.
 * 
 * @author Fernando Ortega
 */
public class AjustedCosine extends UserSimilarities {

	public AjustedCosine(DataModel dataModel) {
		super(dataModel);
	}

	@Override
	public double similarity (TestUser activeUser, User targetUser) {	
		
		int i = 0, j = 0, common = 0; 
		double num = 0d, denActive = 0d, denTarget = 0d;
		
		while (i < activeUser.getNumberOfRatings() && j < targetUser.getNumberOfRatings()) {
			if (activeUser.getItemAt(i).compareTo(targetUser.getItemAt(j))<0) {
				i++;
			} else if (activeUser.getItemAt(i).compareTo(targetUser.getItemAt(j))>0) {
				j++;
			} else {
				String itemCode = activeUser.getItemAt(i);
				Item item = this.dataModel.getItem(itemCode);
				double avg = item.getDataBank().getDouble(Item.AVERAGERATING_KEY);
				
				double fa = activeUser.getRatingAt(i) - avg;
				double ft = targetUser.getRatingAt(j) - avg;
				
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
