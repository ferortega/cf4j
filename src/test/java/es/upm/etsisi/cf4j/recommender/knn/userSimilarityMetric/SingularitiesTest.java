package es.upm.etsisi.cf4j.recommender.knn.userSimilarityMetric;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.MockDataSet;
import es.upm.etsisi.cf4j.data.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SingularitiesTest {

  private static DataModel datamodel;

  @BeforeAll
  static void initAll() {
    datamodel = new DataModel(new MockDataSet());
  }

  @Test
  void similarity() {
    Singularities sim = new Singularities(new double[] {0.0, 1.0, 4.0}, new double[] {2.0, 3.0});
    sim.setDatamodel(datamodel);
    sim.beforeRun();

    User user0 = datamodel.getUser(0);
    User user1 = datamodel.getUser(1);
    User user2 = datamodel.getUser(3);

    assertEquals(0.0703125, sim.similarity(user0, user1));
    assertEquals(0.10286458333333333, sim.similarity(user0, user2));
    assertEquals(0.076171875, sim.similarity(user1, user2));
    assertTrue(sim.similarity(user0, user1) < sim.similarity(user0, user2));
    assertTrue(sim.similarity(user0, user1) < sim.similarity(user1, user2));
    assertTrue(sim.similarity(user1, user2) < sim.similarity(user0, user2));
    sim.afterRun();
  }
}
