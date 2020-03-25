package es.upm.etsisi.cf4j.qualityMeasures.recommendation;

import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.qualityMeasures.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.utils.Methods;

/**
 * <p>This class calculates the recall of the recommendations performed by a Recommender. It is calculated as
 * follows:</p>
 * <p>recall = &lt;relevant recommended items&gt; / &lt;number of relevant items&gt;</p>
 * @author Fernando Ortega
 */
public class Recall extends QualityMeasure {

	/**
	 * Number of recommended items
	 */
	private int numberOfRecommendations;
	
	/**
	 * Relevant rating threshold
	 */
	private double relevantThreshold;

	/**
	 * Constructor
	 * @param recommender Recommender instance for which the recall are going to be computed
	 * @param numberOfRecommendations Number of recommendations
	 * @param relevantThreshold Minimum rating to consider a rating as relevant
	 */
	public Recall(Recommender recommender, int numberOfRecommendations, double relevantThreshold) {
		super(recommender);
		this.numberOfRecommendations = numberOfRecommendations;
		this.relevantThreshold = relevantThreshold;
	}

	@Override
	public double getScore(TestUser testUser, double[] predictions) {
		
		// Items rated as relevant (in test) by the active user

		int relevant = 0;
		for (int i = 0; i < testUser.getNumberOfTestRatings(); i++){
			double rating = testUser.getTestRatingAt(i);
			if (rating >= this.relevantThreshold) {
				relevant++;
			}
		}
		
		// Items that has been recommended and was relevant to the active user

		int [] recommendations = Methods.findTopN(predictions, this.numberOfRecommendations);
		
		int recommendedAndRelevant = 0;

		for (int i : recommendations) {
			if (i == -1) break;

			double rating = testUser.getTestRatingAt(i);
			if (rating >= this.relevantThreshold) {
				recommendedAndRelevant++;
			}			
		}
		
		double recall = (double) recommendedAndRelevant / (double) relevant;
		return recall;
	}
}
