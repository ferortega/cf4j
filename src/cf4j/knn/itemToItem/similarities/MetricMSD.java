package cf4j.knn.itemToItem.similarities;

import cf4j.Item;
import cf4j.Kernel;
import cf4j.TestItem;

/**
 * Implements traditional MSD as CF similarity metric for items. The returned value is 1 - MSD.
 * 
 * @author Fernando Ortega
 */
public class MetricMSD extends ItemsSimilarities{

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
	public double similarity (TestItem activeItem, Item targetItem) {

		int u = 0, v = 0, common = 0; 
		double msd = 0d;
		
		while (u < activeItem.getNumberOfRatings() && v < targetItem.getNumberOfRatings()) {
			if (activeItem.getUsers()[u] < targetItem.getUsers()[v]) {
				u++;
			} else if (activeItem.getUsers()[u] > targetItem.getUsers()[v]) {
				v++;
			} else {
				double diff = (activeItem.getRatings()[u] - targetItem.getRatings()[v]) / this.maxDiff;
				msd += diff * diff;
				
				common++;
				u++;
				v++;
			}	
		}

		// If there is not ratings in common, similarity does not exists
		if (common == 0) return Double.NEGATIVE_INFINITY;
		
		// Return similarity
		return 1d - (msd / common);
	}
}
