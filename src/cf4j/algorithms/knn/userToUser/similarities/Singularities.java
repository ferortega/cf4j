package cf4j.algorithms.knn.userToUser.similarities;

import java.util.HashSet;

import cf4j.data.Item;
import cf4j.data.DataModel;
import cf4j.data.TestUser;
import cf4j.data.User;

/**
 * Implements the following CF similarity metric: Bobadilla, J., Ortega, F., &amp;
 * Hernando, A. (2012). A collaborative filtering similarity measure based on
 * singularities, Information Processing and Management, 48 (2), 204-217.
 * 
 * @author Fernando Ortega
 */
public class Singularities extends UserSimilarities{

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

		this.maxDiff = dataModel.getDataBank().getDouble(DataModel.MAXRATING_KEY) - dataModel.getDataBank().getDouble(DataModel.MINRATING_KEY);
	}

	@Override
	public void beforeRun () {
		super.beforeRun();
		
		double numUsers = dataModel.getNumberOfUsers();

		// To store items singularity
		this.singularityOfRelevantRatings = new double [dataModel.getNumberOfItems()];
		this.singularityOfNotRelevantRatings = new double [dataModel.getNumberOfItems()];

		for (int i = 0; i < dataModel.getNumberOfItems(); i++) {
			Item item = dataModel.getItemAt(i);

			int numberOfRelevantRatings = 0;
			int numberOfNotRelevantRatings = 0;

			for (int j = 0; j < item.getNumberOfRatings(); j++){
				double rating = item.getRatingAt(j);
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
			if (activeUser.getItems().get(i).compareTo(targetUser.getItems().get(j)) < 0) { //TODO:Check, could be reversed.
				i++;
			} else if (activeUser.getItems().get(i).compareTo(targetUser.getItems().get(j)) > 0) { //TODO:Check, could be reversed.
				j++;
			} else {
				
				// Get the ratings
				String itemCode = activeUser.getItemAt(i);
				int itemIndex = dataModel.getItemIndex(itemCode);
				double activeUserRating = activeUser.getRatings().get(i);
				double targetUserRating = targetUser.getRatings().get(j);

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