package es.upm.etsisi.cf4j.qualityMeasure.recommendation;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.MockDataSet;
import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.recommender.DummyRecommender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DiversityTest {
  private static TestUser testUser;
  private static DataModel dataModel;

  private static final double[] perfect = new double[] {1.0, 2.0};

  @BeforeAll
  static void initAll() {
    dataModel = new DataModel(new MockDataSet());
    testUser = dataModel.getTestUser(0);
  }

  @Test
  void getScore() {
    Diversity metric = new Diversity(new DummyRecommender(dataModel), 2);

    assertEquals(0.9079593845004517, metric.getScore(testUser, perfect));
  }
}
