package es.upm.etsisi.cf4j.qualityMeasure.prediction;

import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;

/**
 * This class calculates the Mean Absolute Difference (MAE) between the predictions and the test ratings.
 * @author Fernando Ortega
 */
public class MAE extends QualityMeasure {

	/**
	 * Constructor
	 * @param recommender Recommender instance for which the MAE are going to be computed
	 */
	public MAE(Recommender recommender) {
		super(recommender);
	}

	@Override
	public double getScore(TestUser testUser, double[] predictions) {

		double sum = 0d;
		int count = 0;
		
		for (int pos = 0; pos < testUser.getNumberOfTestRatings(); pos++) {
			if (!Double.isNaN(predictions[pos])) {
				sum += Math.abs(predictions[pos] - testUser.getTestRatingAt(pos));
				count++;
			}
		}
		
		return (count == 0) ? Double.NaN : (sum / count);
	}
}
