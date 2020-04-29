package es.upm.etsisi.cf4j.recommender.knn.itemSimilarityMetric;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.Item;
import es.upm.etsisi.cf4j.data.MockDataSet;
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

    Item item0 = datamodel.getItem(0);
    Item item1 = datamodel.getItem(1);
    Item item2 = datamodel.getItem(3);

    assertEquals(0.03125, sim.similarity(item0, item1));
    assertEquals(0.036458333333333336, sim.similarity(item0, item2));
    assertEquals(0.078125, sim.similarity(item1, item2));
    sim.afterRun();
  }
}
