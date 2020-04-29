package es.upm.etsisi.cf4j.qualityMeasure.prediction;

import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;

/**
 * This class calculates the the coefficient of determination, usually denoted as R2, of the
 * predictions performed by a recommender.
 *
 * <p>MSE = (&#8721;(&lt;test item rating&gt; - &lt;test item rating prediction&gt;)<sup>2</sup> +
 * (&lt;test item rating&gt; - &lt;getTestRatingAverage&gt;)<sup>2</sup>) / &lt;number of
 * predictions&gt;
 */
public class R2 extends QualityMeasure {

  /**
   * Constructor of the class which basically calls the father's one
   *
   * @param recommender Recommender instance for which the R2 are going to be computed
   */
  public R2(Recommender recommender) {
    super(recommender);
  }

  @Override
  public double getScore(TestUser testUser, double[] predictions) {

    double num = 0.0;
    double den = 0.0;

    int count = 0;

    for (int pos = 0; pos < testUser.getNumberOfTestRatings(); pos++) {
      if (!Double.isNaN(predictions[pos])) {
        num += Math.pow(testUser.getTestRatingAt(pos) - predictions[pos], 2);
        den += Math.pow(testUser.getTestRatingAt(pos) - testUser.getTestRatingAverage(), 2);
        count++;
      }
    }

    if (count < 2 || den == 0) {
      return Double.NaN;
    } else {
      return 1 - num / den;
    }
  }
}
