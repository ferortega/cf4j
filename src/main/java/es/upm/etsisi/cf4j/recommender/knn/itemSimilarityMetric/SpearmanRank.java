package es.upm.etsisi.cf4j.recommender.knn.itemSimilarityMetric;

import es.upm.etsisi.cf4j.data.Item;

/** Implements traditional Spearman Rank as CF similarity metric for the items. */
public class SpearmanRank extends ItemSimilarityMetric {

  @Override
  public double similarity(Item item, Item otherItem) {

    int u = 0, v = 0, common = 0;
    double num = 0d;

    while (u < item.getNumberOfRatings() && v < otherItem.getNumberOfRatings()) {
      if (item.getUserAt(u) < otherItem.getUserAt(v)) {
        u++;
      } else if (item.getUserAt(u) > otherItem.getUserAt(v)) {
        v++;
      } else {
        double diff = item.getRatingAt(u) - otherItem.getRatingAt(v);
        num += diff * diff;
        common++;
        u++;
        v++;
      }
    }

    // If there is not ratings in common, similarity does not exists
    if (common == 0) return Double.NEGATIVE_INFINITY;

    // Return similarity
    return 1d - ((6d * num) / (common * ((common * common) - 1d)));
  }
}
