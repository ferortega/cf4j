package es.upm.etsisi.cf4j.qualityMeasures.prediction;

import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.qualityMeasures.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;

/**
 * This class calculates the Mean Squared Logarithmic Error (MSLE) between the predictions and the test ratings.
 * @author Fernando Ortega
 */
public class MSLE extends QualityMeasure {

	/**
	 * Constructor
	 * @param recommender Recommender instance for which the MSLE are going to be computed
	 */
	public MSLE(Recommender recommender) {
		super(recommender);
	}

	@Override
	public double getScore(TestUser testUser, double[] predictions) {

		double sum = 0d;
		int count = 0;
		
		for (int i = 0; i < testUser.getNumberOfTestRatings(); i++) {
			if (!Double.isNaN(predictions[i])) {
				double diff = Math.log(1 + testUser.getTestRatingAt(i)) - Math.log(1 + predictions[i]);
				sum += diff * diff;
				count++;
			}
		}
		
		return (count == 0) ? Double.NaN : Math.sqrt(sum / count);
	}
}
