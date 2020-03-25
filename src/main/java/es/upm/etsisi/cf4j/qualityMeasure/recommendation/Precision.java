package es.upm.etsisi.cf4j.qualityMeasure.recommendation;

import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.utils.Search;

/**
 * <p>This class calculates the precision of the recommendations performed by a Recommender. It is calculated as
 * follows:</p>
 * <p>precision = &lt;relevant recommended items&gt; / &lt;number of recommended items&gt;</p>
 * @author Fernando Ortega
 */
public class Precision extends QualityMeasure {

	/**
	 * Number of recommended items
	 */
	private int numberOfRecommendations;
	
	/**
	 * Relevant rating threshold
	 */
	private double relevantThreshold;

	/**
	 * Constructor of Precision
	 * @param recommender Recommender instance for which the precision are going to be computed
	 * @param numberOfRecommendations Number of recommendations
	 * @param relevantThreshold Minimum rating to consider a rating as relevant
	 */
	public Precision(Recommender recommender, int numberOfRecommendations, double relevantThreshold) {
		super(recommender);
		this.numberOfRecommendations = numberOfRecommendations;
		this.relevantThreshold = relevantThreshold;
	}

	@Override
	protected double getScore(TestUser testUser, double[] predictions) {
		
		// Items that has been recommended and was relevant to the active user

		int [] recommendations = Search.findTopN(predictions, this.numberOfRecommendations);
		
		int recommendedAndRelevant = 0, recommended = 0;

		for (int i : recommendations) {
			if (i == -1) break;

			double rating = testUser.getRatingAt(i);
			if (rating >= this.relevantThreshold) {
				recommendedAndRelevant++;
			}
			
			recommended++;
		}
		
		double precision = (double) recommendedAndRelevant / (double) recommended;
		return precision;
	}
}
