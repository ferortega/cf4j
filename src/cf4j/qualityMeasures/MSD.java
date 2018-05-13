package cf4j.qualityMeasures;

import cf4j.TestUser;

/**
 * <p>This class calculates the MSD (Mean Square Difference) of the recommender system. The MSD is the
 * quadratic difference between the user rating and the predicted rating.</p>
 * 
 * <p>This class puts the "MSD" key at the Kernel map containing a double with the MSD value.</p>
 * 
 * @author Fernando Ortega
 */
public class MSD extends QualityMeasure {

	private final static String NAME = "MSD";

	/**
	 * Constructor of MSD
	 */
	public MSD () {
		super(NAME);
	}

	@Override
	public double getMeasure (TestUser testUser) {
		
		double [] predictions = testUser.getPredictions();
		double [] ratings = testUser.getTestRatings();
		
		double msd = 0d; 
		int count = 0;
		
		for (int i = 0; i < ratings.length; i++) {
			if (!Double.isNaN(predictions[i])) {
				msd += Math.pow(predictions[i] - ratings[i], 2);
				count++;
			}
		}
		
		return (count == 0) 
			? Double.NaN
			: msd / count;
	}
}
