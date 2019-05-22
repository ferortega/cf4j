package cf4j.qualityMeasures;

import cf4j.data.DataModel;
import cf4j.data.TestUser;

import java.util.ArrayList;

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
	public MAE (DataModel dataModel) {
		super(dataModel, NAME);
	}

	@Override
	public double getMeasure (TestUser testUser) {
		
		Double [] predictions = testUser.getDataBank().getDoubleArray(TestUser.PREDICTIONS_KEYS);
		ArrayList<Double> ratings = testUser.getTestRatings();
		
		double mae = 0d; 
		int count = 0;
		
		for (int i = 0; i < ratings.size() ; i++) {
			if (!Double.isNaN(predictions[i])) {
				mae += Math.abs(predictions[i] - ratings.get(i));
				count++;
			}
		}
		
		return (count == 0) 
			? Double.NaN
			: mae / count;
	}
}
