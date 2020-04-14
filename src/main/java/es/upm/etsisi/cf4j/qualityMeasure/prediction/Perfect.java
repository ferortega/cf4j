package es.upm.etsisi.cf4j.qualityMeasure.prediction;

import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;

/**
 * This class calculates the percentage of perfect predictions. A prediction is considered perfect if and only if
 * the absolute difference between the test rating and the prediction is less or equal than a threshold.
 */
public class Perfect extends QualityMeasure {

	/**
	 * Threshold value to measure if a prediction is perfect or not
	 */
	private double threshold;

	/**
	 * Constructor of the class which basically calls the father's one
	 * @param recommender Recommender instance for which the perfect score are going to be computed
	 * @param threshold Threshold value to measure if a prediction is perfect or not
	 */
	public Perfect(Recommender recommender, double threshold) {
		super(recommender);
		this.threshold = threshold;
	}

	@Override
	public double getScore(TestUser testUser, double[] predictions) {

		int hits = 0;
		int total = 0;
		
		for (int pos = 0; pos < testUser.getNumberOfTestRatings(); pos++) {
			if (!Double.isNaN(predictions[pos])) {
				double diff = Math.abs(predictions[pos] - testUser.getTestRatingAt(pos));
				if (diff <= threshold) {
					hits++;
				}
				total++;
			}
		}
		
		return (total == 0) ? Double.NaN : (double) hits / (double) total;
	}
}
