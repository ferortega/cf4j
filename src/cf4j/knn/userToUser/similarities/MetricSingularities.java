package cf4j.knn.userToUser.similarities;

import java.util.HashSet;

import cf4j.Item;
import cf4j.Kernel;
import cf4j.TestUser;
import cf4j.User;

/**
 * Implements the following CF similarity metric: Bobadilla, J., Ortega, F., &amp;
 * Hernando, A. (2012). A collaborative filtering similarity measure based on
 * singularities, Information Processing and Management, 48 (2), 204-217.
 * 
 * @author Fernando Ortega
 */
public class MetricSingularities extends UsersSimilarities{

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
		
		this.maxDiff = Kernel.gi().getMaxRating() - Kernel.gi().getMinRating();
	}

	@Override
	public void beforeRun () {
		super.beforeRun();
		
		double numUsers = Kernel.gi().getNumberOfUsers();

		// To store items singularity
		this.singularityOfRelevantRatings = new double [Kernel.gi().getNumberOfItems()];
		this.singularityOfNotRelevantRatings = new double [Kernel.gi().getNumberOfItems()];

		for (int i = 0; i < Kernel.gi().getNumberOfItems(); i++) {
			Item item = Kernel.getInstance().getItems()[i];

			int numberOfRelevantRatings = 0;
			int numberOfNotRelevantRatings = 0;

			for (double rating : item.getRatings()) {
				if (relevantRatings.contains(rating)) numberOfRelevantRatings++;
				if (notRelevantRatings.contains(rating)) numberOfNotRelevantRatings++;
			}

			this.singularityOfRelevantRatings[i] = 1d - numberOfRelevantRatings / numUsers;
			this.singularityOfNotRelevantRatings[i] = 1d - numberOfNotRelevantRatings / numUsers;
		}
	}

	@Override
	public double similarity (TestUser activeUser, User targetUser) {		

		// Compute the metric
		//  (a) Both users have rated as relevant
		//  (b) Both users has rated as no relevant
		//  (c) One user has rated relevant and the other one has rated no relevant
		double metric_a = 0d, metric_b = 0d, metric_c = 0d;
		int items_a = 0, items_b = 0, items_c = 0;

		int i = 0, j = 0, common = 0;
		while (i < activeUser.getNumberOfRatings() && j < targetUser.getNumberOfRatings()) {
			if (activeUser.getItems()[i] < targetUser.getItems()[j]) {
				i++;
			} else if (activeUser.getItems()[i] > targetUser.getItems()[j]) {
				j++;
			} else {
				
				// Get the ratings
				int itemCode = activeUser.getItems()[i];
				int itemIndex = Kernel.getInstance().getItemIndex(itemCode);
				double activeUserRating = activeUser.getRatings()[i];
				double targetUserRating = targetUser.getRatings()[j];

				// Both user have rated relevant
				if (this.relevantRatings.contains(activeUserRating) && this.relevantRatings.contains(targetUserRating)) {
					items_a++;

					double sing_p = this.singularityOfRelevantRatings[itemIndex];

					double diff = ((double) (activeUserRating - targetUserRating)) / this.maxDiff;
					metric_a += (1d - diff * diff) * sing_p * sing_p;

				// Both users have rated no relevant
				} else if (this.notRelevantRatings.contains(activeUserRating) && this.notRelevantRatings.contains(targetUserRating)) {
					items_b++;

					double sing_n = this.singularityOfNotRelevantRatings[itemIndex];

					double diff = ((double) (activeUserRating - targetUserRating)) / this.maxDiff;
					metric_b += (1d - diff * diff) * sing_n * sing_n;

				//  One user has rated relevant and the other one has rated no relevat
				} else {
					items_c++;

					double sing_p = this.singularityOfRelevantRatings[itemIndex];
					double sing_n = this.singularityOfNotRelevantRatings[itemIndex];

					double diff = ((double) (activeUserRating - targetUserRating)) / this.maxDiff;
					metric_c += (1d - diff * diff) * sing_p * sing_n;
				}
				
				common++;
				i++; 
				j++;
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