package es.upm.etsisi.cf4j.qualityMeasures.prediction;

import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.qualityMeasures.QualityMeasure;
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
		
		for (int i = 0; i < testUser.getNumberOfTestRatings(); i++) {
			if (!Double.isNaN(predictions[i])) {
				sum += Math.abs(predictions[i] - testUser.getTestRatingAt(i));
				count++;
			}
		}
		
		return (count == 0) ? Double.NaN : (sum / count);
	}
}
