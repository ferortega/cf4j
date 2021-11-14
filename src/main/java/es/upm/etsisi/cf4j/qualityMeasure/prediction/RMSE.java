package es.upm.etsisi.cf4j.qualityMeasure.prediction;

import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.recommender.Recommender;

/**
 * This class calculates the Root Mean Squared Error (RMSE) between the predictions and the test
 * ratings.
 *
 * <p>MSE = &radic;(&#8721;(&lt;test item rating prediction&gt; - &lt;test item
 * rating&gt;)<sup>2</sup> / &lt;number of predictions&gt;)
 */
public class RMSE extends MSE { //QualityMeasure through MSE

  /**
   * Constructor of the class which basically calls the father's one
   *
   * @param recommender Recommender instance for which the RMSE are going to be computed
   */
  public RMSE(Recommender recommender) {
    super(recommender);
  }

  @Override
  public double getScore(TestUser testUser, double[] predictions) {
    double mse = super.getScore(testUser, predictions);
    return (Double.isNaN(mse)) ? Double.NaN : Math.sqrt(mse);
  }
}
