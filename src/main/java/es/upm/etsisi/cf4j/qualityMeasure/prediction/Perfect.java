package es.upm.etsisi.cf4j.qualityMeasure.prediction;

import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;

/**
 * This class calculates the percentage of perfect predictions. A prediction is considered perfect if and only if
 * the absolute difference between the test rating and the prediction is less or equal than a threshold.
 * @author Fernando Ortega
 */
public class Perfect extends QualityMeasure {

	/**
	 * Threshold value to measure if a prediction is perfect or not
	 */
	private double threshold;

	/**
	 * Constructor
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
		
		for (int i = 0; i < testUser.getNumberOfTestRatings(); i++) {
			if (!Double.isNaN(predictions[i])) {
				double diff = Math.abs(predictions[i] - testUser.getTestRatingAt(i));
				if (diff <= threshold) {
					hits++;
				}
				total++;
			}
		}
		
		return (total == 0) ? Double.NaN : (double) hits / (double) total;
	}
}
