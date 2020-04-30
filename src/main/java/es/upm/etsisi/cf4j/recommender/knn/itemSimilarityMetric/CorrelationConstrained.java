package es.upm.etsisi.cf4j.recommender.knn.itemSimilarityMetric;

import es.upm.etsisi.cf4j.data.Item;

/** This class implements the Constrained Correlation as CF similarity metric for items. */
public class CorrelationConstrained extends ItemSimilarityMetric {

  /** Median of the ratings of the dataset */
  private double median;

  /**
   * Constructor of the similarity metric
   *
   * @param median Median of the ratings of the DataSet instance
   */
  public CorrelationConstrained(double median) {
    this.median = median;
  }

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
        double fa = item.getRatingAt(u) - this.median;
        double ft = otherItem.getRatingAt(v) - this.median;

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

  @Override
  public String toString() {
    return super.toString() + "(median=" + this.median + ")";
  }
}
