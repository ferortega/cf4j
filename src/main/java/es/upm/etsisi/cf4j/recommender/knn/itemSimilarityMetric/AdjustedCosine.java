package es.upm.etsisi.cf4j.recommender.knn.itemSimilarityMetric;

import es.upm.etsisi.cf4j.data.Item;
import es.upm.etsisi.cf4j.data.User;

/** Implements traditional Adjusted Cosine as CF similarity metric for the items. */
public class AdjustedCosine extends ItemSimilarityMetric {

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
        int userIndex = item.getUserAt(u);
        User user = super.datamodel.getUser(userIndex);
        double avg = user.getRatingAverage();

        double fa = item.getRatingAt(u) - avg;
        double ft = otherItem.getRatingAt(v) - avg;

        num += fa * ft;
        denActive += fa * fa;
        denTarget += ft * ft;

        common++;
        u++;
        v++;
      }
    }

    // If there is not ratings in common, similarity does not exists
    if (common == 0) return Double.NEGATIVE_INFINITY;

    // Denominator can not be zero
    if (denActive == 0 || denTarget == 0) return Double.NEGATIVE_INFINITY;

    // Return similarity
    return num / Math.sqrt(denActive * denTarget);
  }
}
