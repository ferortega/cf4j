package es.upm.etsisi.cf4j.recommender.knn.itemSimilarityMetric;

import es.upm.etsisi.cf4j.data.Item;

public class ItemSimilarityMetricMock extends ItemSimilarityMetric {

  @Override
  public double similarity(Item item, Item otherItem) {
    return 0.5;
  }
}
