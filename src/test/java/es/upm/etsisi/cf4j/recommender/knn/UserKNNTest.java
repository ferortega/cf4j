package es.upm.etsisi.cf4j.recommender.knn;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.MockDataSet;
import es.upm.etsisi.cf4j.recommender.knn.userSimilarityMetric.UserSimilarityMetricMock;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserKNNTest {

  private static final int numberOfNeighbors = 2;

  private static final int testUserId = 1;
  private static final int testItemId = 1;

  private static DataModel datamodel;

  @BeforeAll
  static void initAll() {
    datamodel = new DataModel(new MockDataSet());
  }

  @Test
  void userKNNTest() {

    // DEVIATION_FROM_MEAN
    UserKNN uKNN =
        new UserKNN(
            datamodel,
            numberOfNeighbors,
            new UserSimilarityMetricMock(),
            UserKNN.AggregationApproach.DEVIATION_FROM_MEAN);
    uKNN.fit();
    assertEquals(1.8333333333333335, uKNN.predict(testUserId, testItemId));
    assertEquals(
        uKNN.predict(testUserId, testItemId),
        uKNN.predict(datamodel.getTestUser(testUserId))[testItemId]);

    // WEIGHTED_MEAN
    uKNN =
        new UserKNN(
            datamodel,
            numberOfNeighbors,
            new UserSimilarityMetricMock(),
            UserKNN.AggregationApproach.WEIGHTED_MEAN);
    uKNN.fit();
    assertEquals(1.0, uKNN.predict(testUserId, testItemId));
    assertEquals(
        uKNN.predict(testUserId, testItemId),
        uKNN.predict(datamodel.getTestUser(testUserId))[testItemId]);

    // Mean
    uKNN =
        new UserKNN(
            datamodel,
            numberOfNeighbors,
            new UserSimilarityMetricMock(),
            UserKNN.AggregationApproach.MEAN);
    uKNN.fit();
    assertEquals(1.0, uKNN.predict(testUserId, testItemId));
    assertEquals(
        uKNN.predict(testUserId, testItemId),
        uKNN.predict(datamodel.getTestUser(testUserId))[testItemId]);
  }
}
