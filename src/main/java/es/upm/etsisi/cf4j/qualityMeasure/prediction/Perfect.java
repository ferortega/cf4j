package es.upm.etsisi.cf4j.qualityMeasure.prediction;

import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;

import java.util.Map;

/**
 * This class calculates the percentage of perfect predictions. A prediction is considered perfect
 * if and only if the absolute difference between the test rating and the prediction is less or
 * equal than a threshold.
 */
public class Perfect extends QualityMeasure {

  /** Threshold value to measure if a prediction is perfect or not */
  private double threshold;

  /**
   * Constructor from a Map object with the quality measure parameters. Map object must contains the
   * following keys:
   *
   * <ul>
   *   <li><b>threshold</b>: double value that defines the allowed threshold to measure if a
   *       prediction is perfect or not.
   * </ul>
   *
   * @param recommender Recommender instance for which the Perfect score are going to be computed
   * @param params Quality measure's parameters
   */
  public Perfect(Recommender recommender, Map<String, Object> params) {
    this(recommender, (double) params.get("threshold"));
  }

  /**
   * Constructor of the class which basically calls the father's one
   *
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
