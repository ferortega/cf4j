package es.upm.etsisi.cf4j.qualityMeasures.recommendation;

import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.qualityMeasures.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.utils.Methods;

/**
 * <p>This class calculates the Precision of the recommender system. It is calculated as 
 * follows:</p>
 * 
 * <p>precision = &lt;relevant recommended items&gt; / &lt;number of recommended items&gt;</p>
 *
 * <p>This class puts the "Precision" key at the Kernel map containing a double with the 
 * precision value.</p>
 * 
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
	 * @param numberOfRecommendations Number of recommendations
	 * @param relevantThreshold Minimum rating to consider a rating as relevant
	 */
	public Precision (Recommender recommender, int numberOfRecommendations, double relevantThreshold) {
		super(recommender);
		this.numberOfRecommendations = numberOfRecommendations;
		this.relevantThreshold = relevantThreshold;
	}

	@Override
	protected double getScore(TestUser testUser, double[] predictions) {
		
		// Items that has been recommended and was relevant to the active user
		
		int [] recommendations = Methods.findTopN(predictions, this.numberOfRecommendations);
		
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
