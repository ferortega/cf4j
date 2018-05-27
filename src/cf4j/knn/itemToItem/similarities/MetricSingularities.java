package cf4j.knn.itemToItem.similarities;

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
public class MetricSingularities extends ItemsSimilarities{

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
	public MetricSingularities (double [] relevantRatings, double [] notRelevantRatings) {

		this.relevantRatings = new HashSet <Double> ();
		for (double r : relevantRatings) this.relevantRatings.add(r);

		this.notRelevantRatings = new HashSet <Double> ();
		for (double r : notRelevantRatings)  this.notRelevantRatings.add(r);
		
		this.maxDiff = DataModel.gi().getMaxRating() - DataModel.gi().getMinRating();
	}

	@Override
	public void beforeRun () {
		super.beforeRun();
		
		double numItems = DataModel.gi().getNumberOfItems();

		// To store users singularity
		this.singularityOfRelevantRatings = new double [DataModel.gi().getNumberOfUsers()];
		this.singularityOfNotRelevantRatings = new double [DataModel.gi().getNumberOfUsers()];

		for (int u = 0; u < DataModel.gi().getNumberOfUsers(); u++) {
			User user = DataModel.gi().getUsers()[u];

			int numberOfRelevantRatings = 0;
			int numberOfNotReleavantRatings = 0;

			for (double rating : user.getRatings()) {
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
			if (activeItem.getUsers()[u] < targetItem.getUsers()[v]) {
				u++;
			} else if (activeItem.getUsers()[u] > targetItem.getUsers()[v]) {
				v++;
			} else {
				
				// Get the ratings
				int userCode = activeItem.getUsers()[u];
				int userIndex = DataModel.getInstance().getUserIndex(userCode);
				double activeItemRating = activeItem.getRatings()[u];
				double targetItemRating = targetItem.getRatings()[v];

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