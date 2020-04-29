package es.upm.etsisi.cf4j.qualityMeasure.prediction;

import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.data.TestUser;

/**
 * This class calculates the averaged maximum prediction absolute error in a the prediction of a
 * test rating for each test user.
 *
 * <p>max = Max(abs( &lt;test item rating prediction&gt; - &lt;test item rating&gt;))
 */
public class Max extends QualityMeasure {

  /**
   * Constructor of the class which basically calls the father's one.
   *
   * @param recommender Recommender instance for which the max. error are going to be computed.
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
