package es.upm.etsisi.cf4j.qualityMeasure.prediction;

import es.upm.etsisi.cf4j.data.TestUser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MSETest {

  private static TestUser user;
  private static final double[] perfectPrediction = new double[] {2.0, 3.0, 1.0, 5.0, 4.0, 3.0};
  private static final double[] slightlyPrefectPrediction =
      new double[] {2.0, 2.0, 1.0, 4.0, 4.0, 3.0};
  private static final double[] allMinimumPrediction = new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
  private static final double[] allMaximumPrediction = new double[] {5.0, 5.0, 5.0, 5.0, 5.0, 5.0};

  @BeforeAll
  static void initAll() {
    user = new TestUser("101", 101, 101);
    user.addTestRating(0, 2.0);
    user.addTestRating(1, 3.0);
    user.addTestRating(2, 1.0);
    user.addTestRating(3, 5.0);
    user.addTestRating(4, 4.0);
    user.addTestRating(5, 3.0);
  }

  @Test
  void getScore() {
    MSE metric = new MSE(null); // Recommender not needed;

    assertEquals(0.0, metric.getScore(user, perfectPrediction));
    assertEquals(0.3333333333333333, metric.getScore(user, slightlyPrefectPrediction));
    assertEquals(5.666666666666667, metric.getScore(user, allMinimumPrediction));
    assertEquals(5.666666666666667, metric.getScore(user, allMaximumPrediction));
  }
}
