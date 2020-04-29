package es.upm.etsisi.cf4j.recommender.knn.userSimilarityMetric;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.MockDataSet;
import es.upm.etsisi.cf4j.data.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JaccardTest {

  private static DataModel datamodel;

  @BeforeAll
  static void initAll() {
    datamodel = new DataModel(new MockDataSet());
  }

  @Test
  void similarity() {
    Jaccard sim = new Jaccard();
    sim.setDatamodel(datamodel);
    sim.beforeRun();

    User user0 = datamodel.getUser(0);
    User user1 = datamodel.getUser(1);
    User user2 = datamodel.getUser(3);

    assertEquals(1.0, sim.similarity(user0, user1));
    assertEquals(0.5, sim.similarity(user0, user2));
    assertEquals(0.5, sim.similarity(user1, user2));
    assertTrue(sim.similarity(user0, user1) > sim.similarity(user0, user2));
    assertTrue(sim.similarity(user0, user1) > sim.similarity(user1, user2));
    assertEquals(sim.similarity(user1, user2), sim.similarity(user0, user2));
    sim.afterRun();
  }
}
