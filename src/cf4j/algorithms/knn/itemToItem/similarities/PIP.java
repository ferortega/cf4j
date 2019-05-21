package cf4j.algorithms.knn.itemToItem.similarities;

import cf4j.data.Item;
import cf4j.data.DataModel;
import cf4j.data.TestItem;
import cf4j.data.User;

/**
 * This class implements the PIP CF similarity metric for the items. The similarity metric
 * is described here: Ahn, H. J. (2008). A new similarity measure for collaborative filtering
 * to alleviate the new user cold-starting problem, Information Sciences, 178, 37??51.
 * 
 * @author Fernando ortega
 */
public class PIP extends ItemSimilarities {

	/**
	 * Median of the ratings of the dataset
	 */
	private double median;
	
	/**
	 * Maximum rating value
	 */
	private double max;
	
	/**
	 * Minimum rating value
	 */
	private double min;
	
	/**
	 * Constructor of the similarity metric
	 */
	public PIP (DataModel dataModel) {
		super(dataModel);

		this.max = this.dataModel.getDataBank().getDouble(DataModel.MAXRATING_KEY);
		this.min = this.dataModel.getDataBank().getDouble(DataModel.MINRATING_KEY);
		
		this.median = ((double) (this.dataModel.getDataBank().getDouble(DataModel.MAXRATING_KEY) + this.dataModel.getDataBank().getDouble(DataModel.MINRATING_KEY))) / 2d;
	}
	
	@Override
	public double similarity (TestItem activeItem, Item targetItem) {	

		int u = 0, v = 0, common = 0; 
		double PIP = 0d;
		
		while (u < activeItem.getNumberOfRatings() && v < targetItem.getNumberOfRatings()) {
			if (activeItem.getUserAt(u).compareTo(targetItem.getUserAt(v)) < 0) {
				u++;
			} else if (activeItem.getUserAt(u).compareTo(targetItem.getUserAt(v)) > 0) {
				v++;
			} else {
				double ra = activeItem.getRatingAt(u);
				double rt = targetItem.getRatingAt(v);

				// Compute agreement
				boolean agreement = true;
				if ((ra > this.median && rt < this.median) || (ra < this.median && rt > this.median)) {
					agreement = false;
				}

				// Compute proximity
				double d = (agreement) ? Math.abs(ra - rt) : 2 * Math.abs(ra - rt);
				double proximity = ((2d * (this.max - this.min) + 1d) - d) * ((2d * (this.max - this.min) + 1d) - d);

				// Calculamos el impact
				double im = (Math.abs(ra - this.median) + 1d) * (Math.abs(rt - this.median) + 1d);
				double impact = (agreement) ? im : 1d / im;

				// Calculamos la popularity
				String userCode = activeItem.getUserAt(u);
				User user = this.dataModel.getUser(userCode);
				double userAvg = (user.getNumberOfRatings() != 0) ? user.getDataBank().getDouble(User.AVERAGERATING_KEY) : 0;
				
				double popularity = 1;
				if ((ra > userAvg && rt > userAvg) || (ra < userAvg && rt < userAvg)) {
					popularity = 1d + Math.pow(((ra + rt) / 2d) - userAvg, 2d);
				}

				// Increment PIP
				PIP += proximity * impact * popularity;

				common++;
				u++;
				v++;
			}	
		}

		// If there is not ratings in common, similarity does not exists
		if (common == 0) return Double.NEGATIVE_INFINITY;

		// Return similarity
		return PIP;
	}
}
