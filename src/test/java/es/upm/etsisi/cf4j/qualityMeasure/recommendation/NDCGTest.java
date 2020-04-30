package es.upm.etsisi.cf4j.qualityMeasure.recommendation;

import es.upm.etsisi.cf4j.data.TestUser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NDCGTest {
  private static TestUser user;
  private static final double[] perfectPrediction = new double[] {2.0, 3.0, 1.0, 5.0, 4.0, 3.0};
  private static final double[] randomPrediction = new double[] {3.0, 2.0, 5.0, 4.0, 4.0, 1.0};
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
    NDCG metric = new NDCG(null, 6); // Recommender not needed;

    assertEquals(1.0, metric.getScore(user, perfectPrediction));
    assertEquals(0.7124848526889838, metric.getScore(user, randomPrediction));
    assertEquals(0.6096185378873756, metric.getScore(user, allMinimumPrediction));
    assertEquals(0.6096185378873756, metric.getScore(user, allMaximumPrediction));
  }
}
