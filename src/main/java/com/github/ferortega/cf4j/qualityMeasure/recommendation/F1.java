package com.github.ferortega.cf4j.qualityMeasure.recommendation;

import com.github.ferortega.cf4j.data.TestUser;
import com.github.ferortega.cf4j.qualityMeasure.QualityMeasure;
import com.github.ferortega.cf4j.recommender.Recommender;
import com.github.ferortega.cf4j.util.Search;

/**
 * <p>This class calculates the F1 score of the recommender system. F1 score is computed as follows:</p>
 * <p>F1 = 2 * precision * recall / (precision + recall)</p>
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
	 * Constructor
	 * @param recommender Recommender instance for which the F1 are going to be computed
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
		for (int pos = 0; pos < testUser.getNumberOfTestRatings(); pos++){
			double rating = testUser.getTestRatingAt(pos);
			if (rating >= this.relevantThreshold) {
				relevant++;
			}
		}

		// Items that has been recommended and was relevant to the test user
		int [] recommendations = Search.findTopN(predictions, this.numberOfRecommendations);

		int recommendedAndRelevant = 0, recommended = 0;

		for (int pos : recommendations) {
			if (pos == -1) break;

			double rating = testUser.getTestRatingAt(pos);
			if (rating >= this.relevantThreshold) {
				recommendedAndRelevant++;
			}

			recommended++;
		}

		// Precision y Recall
		double precision = (double) recommendedAndRelevant / (double) recommended;
		double recall = (double) recommendedAndRelevant / (double) relevant;

		// F1 score
		return 2 * precision * recall / (precision + recall);
	}
}
