package cf4j.algorithms.knn.itemToItem.similarities;

import java.util.HashSet;

import cf4j.data.Item;
import cf4j.data.DataModel;
import cf4j.data.TestItem;
import cf4j.data.User;

/**
 * This class implements the singularities CF similarity metric. The similarity metric 
 * is described here: Bobadilla, J., Ortega, F., 	&amp; Hernando, A. (2012). A collaborative filtering
 * similarity measure based on singularities, Information Processing and Management, 48 (2), 204-217
 * 
 * @author Fernando Ortega
 */
public class Singularities extends ItemSimilarities{

	/**
	 * Maximum difference between the ratings
	 */
	private double maxDiff;
	
	/**
	 * Relevant ratings set
	 */
	private HashSet <Double> relevantRatings;

	/**
	 * Not relevant ratings set
	 */
	private HashSet <Double> notRelevantRatings;

	/**
	 * Singularity of the relevant ratings
	 */
	private double [] singularityOfRelevantRatings;

	/**
	 * Singularity of the not relevant ratings
	 */
	private double [] singularityOfNotRelevantRatings;

	/**
	 * Constructor of the similarity metric
	 * @param relevantRatings Relevant ratings array
	 * @param notRelevantRatings Not relevant ratings array
	 */
	public Singularities (DataModel dataModel, double [] relevantRatings, double [] notRelevantRatings) {
		super(dataModel);

		this.relevantRatings = new HashSet <Double> ();
		for (double r : relevantRatings) this.relevantRatings.add(r);

		this.notRelevantRatings = new HashSet <Double> ();
		for (double r : notRelevantRatings)  this.notRelevantRatings.add(r);
	}

	@Override
	public void beforeRun () {
		super.beforeRun();

		this.maxDiff = this.dataModel.getDataBank().getDouble(DataModel.MAXRATING_KEY) - this.dataModel.getDataBank().getDouble(DataModel.MINRATING_KEY);
		
		double numItems = this.dataModel.getNumberOfItems();

		// To store users singularity
		this.singularityOfRelevantRatings = new double [this.dataModel.getNumberOfUsers()];
		this.singularityOfNotRelevantRatings = new double [this.dataModel.getNumberOfUsers()];

		for (int u = 0; u < this.dataModel.getNumberOfUsers(); u++) {
			User user = this.dataModel.getUserAt(u);

			int numberOfRelevantRatings = 0;
			int numberOfNotReleavantRatings = 0;

			for (int v = 0; v < user.getNumberOfRatings();v++){
				double rating = user.getRatingAt(v);
				if (relevantRatings.contains(rating)) numberOfRelevantRatings++;
				if (notRelevantRatings.contains(rating)) numberOfNotReleavantRatings++;
			}

			this.singularityOfRelevantRatings[u] = 1d - numberOfRelevantRatings / numItems;
			this.singularityOfNotRelevantRatings[u] = 1d - numberOfNotReleavantRatings / numItems;
		}
	}

	@Override
	public double similarity (TestItem activeItem, Item targetItem) {		

		// Compute the metric
		//  (a) Both users have rated as relevant
		//  (b) Both users has rated as no relevant
		//  (c) One user has rated relevant and the other one has rated no relevant
		double metric_a = 0d, metric_b = 0d, metric_c = 0d;
		int items_a = 0, items_b = 0, items_c = 0;

		int u = 0, v = 0, common = 0;
		while (u < activeItem.getNumberOfRatings() && v < targetItem.getNumberOfRatings()) {
			if (activeItem.getUserAt(u).compareTo(targetItem.getUserAt(v))<0) {
				u++;
			} else if (activeItem.getUserAt(u).compareTo(targetItem.getUserAt(v))>0) {
				v++;
			} else {
				
				// Get the ratings
				String userCode = activeItem.getUserAt(u);
				int userIndex = this.dataModel.getUserIndex(userCode);
				double activeItemRating = activeItem.getRatingAt(u);
				double targetItemRating = targetItem.getRatingAt(v);

				// Both user have rated relevant
				if (this.relevantRatings.contains(activeItemRating) && this.relevantRatings.contains(targetItemRating)) {
					items_a++;

					double sing_p = this.singularityOfRelevantRatings[userIndex];

					double diff = ((double) (activeItemRating - targetItemRating)) / this.maxDiff;
					metric_a += (1d - diff * diff) * sing_p * sing_p;

				// Both users have rated no relevant
				} else if (this.notRelevantRatings.contains(activeItemRating) && this.notRelevantRatings.contains(targetItemRating)) {
					items_b++;

					double sing_n = this.singularityOfNotRelevantRatings[userIndex];

					double diff = ((double) (activeItemRating - targetItemRating)) / this.maxDiff;
					metric_b += (1d - diff * diff) * sing_n * sing_n;

				//  One user has rated relevant and the other one has rated no relevat
				} else {
					items_c++;

					double sing_p = this.singularityOfRelevantRatings[userIndex];
					double sing_n = this.singularityOfNotRelevantRatings[userIndex];

					double diff = ((double) (activeItemRating - targetItemRating)) / this.maxDiff;
					metric_c += (1d - diff * diff) * sing_p * sing_n;
				}
				
				common++;
				u++; 
				v++;
			}	
		}

		// If there is not items in common, similarity does not exists
		if (common == 0) return Double.NEGATIVE_INFINITY;

		// Normalization
		if (items_a != 0) metric_a = metric_a / (double) items_a;
		if (items_b != 0) metric_b = metric_b / (double) items_b;
		if (items_c != 0) metric_c = metric_c / (double) items_c;

		// Return similarity
		return (metric_a + metric_b + metric_c) / 3d;
	}
}