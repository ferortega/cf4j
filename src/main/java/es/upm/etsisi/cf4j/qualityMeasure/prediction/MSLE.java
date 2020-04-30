package es.upm.etsisi.cf4j.qualityMeasure.prediction;

import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;

/**
 * This class calculates the Mean Squared Logarithmic Error (MSLE) between the predictions and the
 * test ratings.
 *
 * <p>MSLE = (Ln(1 + &lt;test item rating prediction&gt;) - Ln(1 + &lt;test item
 * rating&gt;))<sup>2</sup>
 */
public class MSLE extends QualityMeasure {

  /**
   * Constructor of the class which basically calls the father's one
   *
   * @param recommender Recommender instance for which the MSLE are going to be computed
   */
  public MSLE(Recommender recommender) {
    super(recommender);
  }

  @Override
  public double getScore(TestUser testUser, double[] predictions) {

    double sum = 0d;
    int count = 0;

    for (int pos = 0; pos < testUser.getNumberOfTestRatings(); pos++) {
      if (!Double.isNaN(predictions[pos])) {
        double diff = Math.log(1 + testUser.getTestRatingAt(pos)) - Math.log(1 + predictions[pos]);
        sum += diff * diff;
        count++;
      }
    }

    return (count == 0) ? Double.NaN : Math.sqrt(sum / count);
  }
}
