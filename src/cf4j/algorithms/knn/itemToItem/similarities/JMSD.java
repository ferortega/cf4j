package cf4j.algorithms.knn.itemToItem.similarities;

import cf4j.data.Item;
import cf4j.data.DataModel;
import cf4j.data.TestItem;

/**
 * This class implements JMSD as the similarity metric for the items. The similarity metric
 * is described in: Bobadilla, J., Serradilla, F., &amp; Bernal, J. (2010). A new collaborative
 * filtering metric that improves the behavior of Recommender Systems, Knowledge-Based Systems,
 * 23 (6), 520-528.
 * 
 * @author Fernando Ortega
 */
public class JMSD extends ItemSimilarities{

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
		this.maxDiff = this.dataModel.getMaxRating() - this.dataModel.getMinRating();
	}

	@Override
	public double similarity (TestItem activeItem, Item targetItem) {

		int u = 0, v = 0, intersection = 0; 
		double msd = 0d;
		
		while (u < activeItem.getNumberOfRatings() && v < targetItem.getNumberOfRatings()) {
			if (activeItem.getUsers().get(u).compareTo(targetItem.getUsers().get(v)) < 0) {
				u++;
			} else if (activeItem.getUsers().get(u).compareTo(targetItem.getUsers().get(v)) > 0) {
				v++;
			} else {
				double diff = (activeItem.getRatings().get(u) - targetItem.getRatings().get(v)) / this.maxDiff;
				msd += diff * diff;
				intersection++;
				u++; 
				v++;
			}	
		}

		// If there is not ratings in common, similarity does not exists
		if (intersection == 0) return Double.NEGATIVE_INFINITY;
		
		// Return similarity
		double union = activeItem.getNumberOfRatings() + targetItem.getNumberOfRatings() - intersection;
		double jaccard = intersection / union;
		return jaccard * (1d - (msd / intersection));
	}
}
