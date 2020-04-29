package es.upm.etsisi.cf4j.recommender.knn.itemSimilarityMetric;

import es.upm.etsisi.cf4j.data.Item;

/** Implements Cosine as CF similarity metric for the items. */
public class Cosine extends ItemSimilarityMetric {

  @Override
  public double similarity(Item item, Item otherItem) {
    int u = 0, v = 0, common = 0;
    double num = 0d, denActive = 0d, denTarget = 0d;

    while (u < item.getNumberOfRatings() && v < otherItem.getNumberOfRatings()) {
      if (item.getUserAt(u) < otherItem.getUserAt(v)) {
        u++;
      } else if (item.getUserAt(u) > otherItem.getUserAt(v)) {
        v++;
      } else {
        num += item.getRatingAt(u) * otherItem.getRatingAt(v);
        denActive += item.getRatingAt(u) * item.getRatingAt(u);
        denTarget += otherItem.getRatingAt(v) * otherItem.getRatingAt(v);

        common++;
        u++;
        v++;
      }
    }

    // If there is not ratings in common, similarity does not exists
    if (common == 0) return Double.NEGATIVE_INFINITY;

    // Return similarity
    return num / (Math.sqrt(denActive) * Math.sqrt(denTarget));
  }
}
