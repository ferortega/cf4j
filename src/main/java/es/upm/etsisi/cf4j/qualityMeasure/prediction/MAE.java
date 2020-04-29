package es.upm.etsisi.cf4j.qualityMeasure.prediction;

import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.data.TestUser;

/**
 * This class calculates the Mean Absolute Error (MAE) between the predictions and the test ratings.
 *
 * <p>mae = &#8721; abs( &lt;test item rating prediction&gt; - &lt;test item rating&gt;) /
 * &lt;number of predictions&gt;
 */
public class MAE extends QualityMeasure {

  /**
   * Constructor of the class which basically calls the father's one
   *
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
