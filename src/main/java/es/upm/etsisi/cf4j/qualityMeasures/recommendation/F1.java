package es.upm.etsisi.cf4j.qualityMeasures.recommendation;

import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.qualityMeasures.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.utils.Methods;

/**
 * <p>This class calculates the F1 score of the recommender system. It is as follows:</p>
 * 
 * <p>F1 = 2 * precision * recall / (precision + recall)</p>
 *
 * <p>This class puts the "F1" key at the Kernel map containing a double with the precision 
 * value and the recall value.</p>
 * 
 * @author Fernando Ortega
 */
public class F1 extends QualityMeasure {

	/**
	 * Number of recommended items
	 */
	private int numberOfRecommendations;
	
	/**
	 * Relevant rating threshold
	 */
	private double relevantThreshold;

	/**
	 * Constructor of F1
	 * @param numberOfRecommendations Number of recommendations
	 * @param relevantThreshold Minimum rating to consider a rating as relevant
	 */
	public F1(Recommender recommender, int numberOfRecommendations, double relevantThreshold) {
		super(recommender);
		this.numberOfRecommendations = numberOfRecommendations;
		this.relevantThreshold = relevantThreshold;
	}


	@Override
	protected double getScore(TestUser testUser, double[] predictions) {

		// Items rated as relevant (in test) by the test user
		int relevant = 0;
		for (int i = 0; i < testUser.getNumberOfTestRatings(); i++){
			double rating = testUser.getTestRatingAt(i);
			if (rating >= this.relevantThreshold) {
				relevant++;
			}
		}

		// Items that has been recommended and was relevant to the test user
		int [] recommendations = Methods.findTopN(predictions, this.numberOfRecommendations);

		int recommendedAndRelevant = 0, recommended = 0;

		for (int i : recommendations) {
			if (i == -1) break;

			double rating = testUser.getTestRatingAt(i);
			if (rating >= this.relevantThreshold) {
				recommendedAndRelevant++;
			}

			recommended++;
		}

		// Precision y Recall
		double precision = (double) recommendedAndRelevant / (double) recommended;
		double recall = (double) recommendedAndRelevant / (double) relevant;

		// F1 score
		double f1 = 2 * precision * recall / (precision + recall);
		return f1;
	}
}
