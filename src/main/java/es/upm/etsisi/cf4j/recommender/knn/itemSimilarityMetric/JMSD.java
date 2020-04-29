package es.upm.etsisi.cf4j.recommender.knn.itemSimilarityMetric;

import es.upm.etsisi.cf4j.data.Item;

/**
 * This class implements JMSD as the similarity metric for the items. The similarity metric is
 * described in: Bobadilla, J., Serradilla, F., &amp; Bernal, J. (2010). A new collaborative
 * filtering metric that improves the behavior of Recommender Systems, Knowledge-Based Systems, 23
 * (6), 520-528.
 */
public class JMSD extends ItemSimilarityMetric {

  /** Maximum difference between the ratings */
  private double maxDiff;

  @Override
  public void beforeRun() {
    super.beforeRun();
    this.maxDiff = super.datamodel.getMaxRating() - super.datamodel.getMinRating();
  }

  @Override
  public double similarity(Item item, Item otherItem) {
    int u = 0, v = 0, intersection = 0;
    double msd = 0d;

    while (u < item.getNumberOfRatings() && v < otherItem.getNumberOfRatings()) {
      if (item.getUserAt(u) < otherItem.getUserAt(v)) {
        u++;
      } else if (item.getUserAt(u) > otherItem.getUserAt(v)) {
        v++;
      } else {
        double diff = (item.getRatingAt(u) - otherItem.getRatingAt(v)) / this.maxDiff;
        msd += diff * diff;
        intersection++;
        u++;
        v++;
      }
    }

    // If there is not ratings in common, similarity does not exists
    if (intersection == 0) return Double.NEGATIVE_INFINITY;

    // Return similarity
    double union = item.getNumberOfRatings() + otherItem.getNumberOfRatings() - intersection;
    double jaccard = intersection / union;
    return jaccard * (1d - (msd / intersection));
  }
}
