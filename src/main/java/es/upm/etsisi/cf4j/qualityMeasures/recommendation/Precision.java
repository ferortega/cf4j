package es.upm.etsisi.cf4j.qualityMeasures.recommendation;

import cf4j.data.DataModel;
import cf4j.data.TestUser;
import cf4j.utils.Methods;
import es.upm.etsisi.cf4j.qualityMeasures.QualityMeasure;

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
	
	private final static String NAME = "Precision";
	
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
	public Precision (DataModel dataModel, int numberOfRecommendations, double relevantThreshold) {
		super(dataModel, NAME);
		this.numberOfRecommendations = numberOfRecommendations;
		this.relevantThreshold = relevantThreshold;
	}

	@Override
	public double getMeasure (TestUser testUser) {
		
		// Items that has been recommended and was relevant to the active user
		Double [] predictions = testUser.getDataBank().getDoubleArray(TestUser.PREDICTIONS_KEYS);
		Integer [] recommendations = Methods.findTopN(predictions, this.numberOfRecommendations);
		
		int recommendedAndRelevant = 0, recommended = 0;

		for (int testItemIndex : recommendations) {
			if (testItemIndex == -1) break;
			
			if (testUser.getTestRatingAt(testItemIndex) >= this.relevantThreshold) {
				recommendedAndRelevant++;
			}
			
			recommended++;
		}
		
		double precision = (double) recommendedAndRelevant / (double) recommended;
		return precision;
	}
}
