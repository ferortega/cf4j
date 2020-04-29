package es.upm.etsisi.cf4j.qualityMeasure.recommendation;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.MockDataSet;
import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.recommender.DummyRecommender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NoveltyTest {
  private static TestUser testUser;
  private static DataModel dataModel;

  private static final double[] prediction = new double[] {1.0, 2.0};

  @BeforeAll
  static void initAll() {
    dataModel = new DataModel(new MockDataSet());
    testUser = dataModel.getTestUser(0);
  }

  @Test
  void getScore() {
    Novelty metric = new Novelty(new DummyRecommender(dataModel), 2);

    assertEquals(2.4594316186372973, metric.getScore(testUser, prediction));
  }
}
