package es.upm.etsisi.cf4j.recommender.knn.itemSimilarityMetric;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.Item;
import es.upm.etsisi.cf4j.data.MockDataSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static jdk.nashorn.internal.objects.Global.Infinity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpearmanRankTest {

  private static DataModel datamodel;

  @BeforeAll
  static void initAll() {
    datamodel = new DataModel(new MockDataSet());
  }

  @Test
  void similarity() {
    SpearmanRank sim = new SpearmanRank();
    sim.setDatamodel(datamodel);
    sim.beforeRun();

    Item item0 = datamodel.getItem(0);
    Item item1 = datamodel.getItem(1);

    assertEquals(-19.0, sim.similarity(item0, item1));
    sim.afterRun();
  }
}
