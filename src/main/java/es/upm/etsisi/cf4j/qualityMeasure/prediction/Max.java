package es.upm.etsisi.cf4j.qualityMeasure.prediction;

import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;

/**
 * This class calculates the averaged maximum prediction absolute error in a the prediction of a test rating for each
 * test user..
 * @author Fernando Ortega
 */
public class Max extends QualityMeasure {

	/**
	 * Constructor
	 * @param recommender Recommender instance for which the max. error are going to be computed
	 */
	public Max(Recommender recommender) {
		super(recommender);
	}

	@Override
	public double getScore(TestUser testUser, double[] predictions) {

		double max = Double.NEGATIVE_INFINITY;

		for (int pos = 0; pos < testUser.getNumberOfTestRatings(); pos++) {
			if (!Double.isNaN(predictions[pos])) {
				double error = Math.abs(predictions[pos] - testUser.getTestRatingAt(pos));
				if (error > max) {
					max = error;
				}
			}
		}
		
		return (Double.isInfinite(max)) ? Double.NaN : max;
	}
}
