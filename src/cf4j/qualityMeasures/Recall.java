package cf4j.qualityMeasures;

import cf4j.data.DataModel;
import cf4j.data.TestUser;
import cf4j.utils.Methods;

/**
 * <p>This class calculates the Recall of the recommender system. It is calculate as 
 * follows:</p>
 * 
 * <p>recall = &lt;relevant recommended items&gt; / &lt;number of relevant items&gt;</p>
 *
 * <p>This class puts the "Recall" key at the Kernel map containing a double with the 
 * recall value.</p>
 * 
 * @author Fernando Ortega
 */
public class Recall extends QualityMeasure {
	
	private final static String NAME = "Recall";
	
	/**
	 * Number of recommended items
	 */
	private int numberOfRecommendations;
	
	/**
	 * Relevant rating threshold
	 */
	private double relevantThreshold;

	/**
	 * Constructor of Recall
	 * @param numberOfRecommendations Number of recommendations
	 * @param relevantThreshold Minimum rating to consider a rating as relevant
	 */
	public Recall (DataModel dataModel, int numberOfRecommendations, double relevantThreshold) {
		super(dataModel, NAME);
		this.numberOfRecommendations = numberOfRecommendations;
		this.relevantThreshold = relevantThreshold;
	}

	@Override
	public double getMeasure (TestUser testUser) {
		
		// Items rated as relevant (in test) by the active user		
		int relevant = 0;
		for (int i = 0; i < testUser.getNumberOfTestRatings(); i++){
			double rating = testUser.getTestRatingAt(i);
			if (rating >= this.relevantThreshold) {
				relevant++;
			}
		}
		
		// Items that has been recommended and was relevant to the active user
		Double [] predictions = testUser.getDataBank().getDoubleArray(TestUser.PREDICTIONS_KEYS);
		Integer [] recommendations = Methods.findTopN(predictions, this.numberOfRecommendations);
		
		int recommendedAndRelevant = 0;

		for (int testItemIndex : recommendations) {
			if (testItemIndex == -1) break;
			
			if (testUser.getTestRatingAt(testItemIndex) >= this.relevantThreshold) {
				recommendedAndRelevant++;
			}			
		}
		
		double recall = (double) recommendedAndRelevant / (double) relevant;
		return recall;
	}
}
