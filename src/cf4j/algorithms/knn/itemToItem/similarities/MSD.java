package cf4j.algorithms.knn.itemToItem.similarities;

import cf4j.data.Item;
import cf4j.data.DataModel;
import cf4j.data.TestItem;

/**
 * Implements traditional MSD as CF similarity metric for items. The returned value is 1 - MSD.
 * 
 * @author Fernando Ortega
 */
public class MSD extends ItemSimilarities{

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
		this.maxDiff = this.dataModel.getDataBank().getDouble(DataModel.MAXRATING_KEY) - this.dataModel.getDataBank().getDouble(DataModel.MINRATING_KEY);
	}

	@Override
	public double similarity (TestItem activeItem, Item targetItem) {

		int u = 0, v = 0, common = 0; 
		double msd = 0d;
		
		while (u < activeItem.getNumberOfRatings() && v < targetItem.getNumberOfRatings()) {
			if (activeItem.getUserAt(u).compareTo(targetItem.getUserAt(v)) < 0) {
				u++;
			} else if (activeItem.getUserAt(u).compareTo(targetItem.getUserAt(v)) > 0) {
				v++;
			} else {
				double diff = (activeItem.getRatingAt(u) - targetItem.getRatingAt(v)) / this.maxDiff;
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
