package cf4j.qualityMeasures;

import cf4j.data.TestUser;
import cf4j.qualityMeasures.QualityMeasure;
import cf4j.utils.Methods;

/**
 * <p>This class calculates the NDCG of the recommender system. It is calculated as 
 * follows:</p>
 * 
 * <p>NDCG = &lt;SumDcg&gt; / &lt;SumIdcg&gt;</p>
 *
 * <p>This class puts the "NDCG" key at the Kernel map containing a double with the 
 * NDCG value.</p>
 * 
 * @author Bo Zhu
 */
public class Ndcg extends QualityMeasure {
	
	private final static String NAME = "NDCG";
	
	/**
	 * Number of recommended items, NDCG@N
	 */
	private int numberOfRecommendations;
	

	/**
	 * Constructor of Ndcg
	 * @param numberOfRecommendations Number of recommendations
	 */
	public Ndcg (int numberOfRecommendations) {
		super(NAME);
		this.numberOfRecommendations = numberOfRecommendations;
	}

	@Override
	public double getMeasure (TestUser testUser) {
		
		double [] testRatings = testUser.getTestRatings();
				
        // Compute DCG
		
		double [] predictions = testUser.getPredictions();
		int [] recommendations = Methods.findTopN(predictions, this.numberOfRecommendations);
		
		double dcg = 0d;
		
		for (int i = 0; i < recommendations.length; i++) {
			int testItemIndex = recommendations[i];
			if (testItemIndex == -1) break;
			
			dcg += (Math.pow(2, testRatings[testItemIndex]) - 1) / (Math.log(i + 2) / Math.log(2));
		}
		
		// Compute IDCG
		
		int [] idealRecommendations = Methods.findTopN(testRatings, this.numberOfRecommendations);

		double idcg = 0d;	
		
		for (int i = 0; i < idealRecommendations.length; i++) {
			int testItemIndex = idealRecommendations[i];
			if (testItemIndex == -1) break;
			
			idcg += (Math.pow(2, testRatings[testItemIndex]) - 1) / (Math.log(i + 2) / Math.log(2));
		}
		
		// Compute NDCG
		
		double ndcg = dcg / idcg;
		
		return ndcg;
	}
}