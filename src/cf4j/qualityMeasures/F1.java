package cf4j.qualityMeasures;

import cf4j.TestUser;
import cf4j.utils.Methods;

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
	
	private final static String NAME = "F1";
	
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
	public F1 (int numberOfRecommendations, double relevantThreshold) {
		super(NAME);
		this.numberOfRecommendations = numberOfRecommendations;
		this.relevantThreshold = relevantThreshold;
	}

	@Override
	public double getMeasure (TestUser testUser) {
		
		// Items rated as relevant (in test) by the active user		
		int relevant = 0;
		for (double rating : testUser.getTestRatings()) {
			if (rating >= this.relevantThreshold) {
				relevant++;
			}
		}
		
		// Items that has been recommended and was relevant to the active user
		double [] predictions = testUser.getPredictions();
		int [] recommendations = Methods.findTopN(predictions, this.numberOfRecommendations);
		
		int recommendedAndRelevant = 0, recommended = 0;

		for (int testItemIndex : recommendations) {
			if (testItemIndex == -1) break;
			
			if (testUser.getTestRatings()[testItemIndex] >= this.relevantThreshold) {
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
