package es.upm.etsisi.cf4j.recommender.knn.itemSimilarityMetric;

import es.upm.etsisi.cf4j.data.Item;

/** Implements traditional MSD as CF similarity metric for items. The returned value is 1 - MSD. */
public class MSD extends ItemSimilarityMetric {

  /** Maximum difference between the ratings */
  private double maxDiff;

  @Override
  public void beforeRun() {
    super.beforeRun();
    this.maxDiff = super.datamodel.getMaxRating() - super.datamodel.getMinRating();
  }

  @Override
  public double similarity(Item item, Item otherItem) {

    int u = 0, v = 0, common = 0;
    double msd = 0d;

    while (u < item.getNumberOfRatings() && v < otherItem.getNumberOfRatings()) {
      if (item.getUserAt(u) < otherItem.getUserAt(v)) {
        u++;
      } else if (item.getUserAt(u) > otherItem.getUserAt(v)) {
        v++;
      } else {
        double diff = (item.getRatingAt(u) - otherItem.getRatingAt(v)) / this.maxDiff;
        msd += diff * diff;

        common++;
        u++;
        v++;
      }
    }

    // If there is not ratings in common, similarity does not exists
    if (common == 0) return Double.NEGATIVE_INFINITY;

    // Return similarity
    return 1d - (msd / common);
  }
}
