package es.upm.etsisi.cf4j.recommender.knn.itemSimilarityMetric;

import es.upm.etsisi.cf4j.data.Item;
import es.upm.etsisi.cf4j.data.User;

/**
 * This class implements the PIP CF similarity metric for the items. The similarity metric is
 * described here: Ahn, H. J. (2008). A new similarity measure for collaborative filtering o
 * alleviate the new user cold-starting problem, Information Sciences, 178, 37??51.
 */
public class PIP extends ItemSimilarityMetric {

  /** Median of the ratings of the dataset */
  private double median;

  /** Maximum rating value */
  private double max;

  /** Minimum rating value */
  private double min;

  @Override
  public void beforeRun() {
    super.beforeRun();
    this.max = this.datamodel.getMaxRating();
    this.min = this.datamodel.getMinRating();
    this.median = (this.max + this.min) / 2d;
  }

  @Override
  public double similarity(Item item, Item otherItem) {

    int u = 0, v = 0, common = 0;
    double PIP = 0d;

    while (u < item.getNumberOfRatings() && v < otherItem.getNumberOfRatings()) {
      if (item.getUserAt(u) < otherItem.getUserAt(v)) {
        u++;
      } else if (item.getUserAt(u) > otherItem.getUserAt(v)) {
        v++;
      } else {
        double ra = item.getRatingAt(u);
        double rt = otherItem.getRatingAt(v);

        boolean agreement = true;
        if ((ra > this.median && rt < this.median) || (ra < this.median && rt > this.median)) {
          agreement = false;
        }

        double d = (agreement) ? Math.abs(ra - rt) : 2 * Math.abs(ra - rt);
        double proximity =
            ((2d * (this.max - this.min) + 1d) - d) * ((2d * (this.max - this.min) + 1d) - d);

        double im = (Math.abs(ra - this.median) + 1d) * (Math.abs(rt - this.median) + 1d);
        double impact = (agreement) ? im : 1d / im;

        int userIndex = item.getUserAt(u);
        User user = this.datamodel.getUser(userIndex);
        double userAvg = user.getRatingAverage();

        double popularity = 1;
        if ((ra > userAvg && rt > userAvg) || (ra < userAvg && rt < userAvg)) {
          popularity = 1d + Math.pow(((ra + rt) / 2d) - userAvg, 2d);
        }

        // Increment PIP
        PIP += proximity * impact * popularity;

        common++;
        u++;
        v++;
      }
    }

    // If there is not ratings in common, similarity does not exists
    if (common == 0) return Double.NEGATIVE_INFINITY;

    // Return similarity
    return PIP;
  }
}
