package cf4j.qualityMeasures;

import cf4j.data.DataModel;
import cf4j.data.TestUser;

import java.util.ArrayList;

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
	public MSD (DataModel dataModel) {
		super(dataModel, NAME);
	}

	@Override
	public double getMeasure (TestUser testUser) {
		
		Double [] predictions = testUser.getDataBank().getDoubleArray(TestUser.PREDICTIONS_KEYS);
		ArrayList<Double> ratings = testUser.getTestRatings();
		
		double msd = 0d; 
		int count = 0;
		
		for (int i = 0; i < ratings.size(); i++) {
			if (!Double.isNaN(predictions[i])) {
				msd += Math.pow(predictions[i] - ratings.get(i), 2);
				count++;
			}
		}
		
		return (count == 0) 
			? Double.NaN
			: msd / count;
	}
}
