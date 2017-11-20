package cf4j.qualityMeasures;

import cf4j.TestUser;

/**
 * <p>This class calculates the MAE (Mean Absolute Error) of the recommender system. The MAE is the
 * absolute difference between the user rating and the predicted rating.</p>
 * 
 * <p>This class puts the "MAE" key at the Kernel map containing a double with the MAE value.</p>
 * 
 * @author Fernando Ortega
 */
public class MAE extends QualityMeasure {

	private final static String NAME = "MAE";

	/**
	 * Constructor of MAE
	 */
	public MAE () {
		super(NAME);
	}

	@Override
	public double getMeasure (TestUser testUser) {
		
		double [] predictions = testUser.getPredictions();
		double [] ratings = testUser.getTestRatings();
		
		double mae = 0d; 
		int count = 0;
		
		for (int i = 0; i < ratings.length; i++) {
			if (!Double.isNaN(predictions[i])) {
				mae += Math.abs(predictions[i] - ratings[i]);
				count++;
			}
		}
		
		return (count == 0) 
			? Double.NaN
			: mae / count;
	}
}
