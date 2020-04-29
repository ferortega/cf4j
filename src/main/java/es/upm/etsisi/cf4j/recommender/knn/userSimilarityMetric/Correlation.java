package es.upm.etsisi.cf4j.recommender.knn.userSimilarityMetric;

import es.upm.etsisi.cf4j.data.User;

/** Implements traditional Pearson Correlation as CF similarity metric. */
public class Correlation extends UserSimilarityMetric {

  @Override
  public double similarity(User user, User otherUser) {

    int i = 0, j = 0, common = 0;
    double num = 0d, denActive = 0d, denTarget = 0d;

    while (i < user.getNumberOfRatings() && j < otherUser.getNumberOfRatings()) {
      if (user.getItemAt(i) < otherUser.getItemAt(j)) {
        i++;
      } else if (user.getItemAt(i) > otherUser.getItemAt(j)) {
        j++;
      } else {
        double t = user.getRatingAt(i) - user.getRatingAverage();
        double o = otherUser.getRatingAt(j) - otherUser.getRatingAverage();

        num += t * o;
        denActive += t * t;
        denTarget += o * o;

        common++;
        i++;
        j++;
      }
    }

    // If there is not items in common, similarity does not exists
    if (common == 0) return Double.NEGATIVE_INFINITY;

    // Denominator can not be zero
    if (denActive == 0 || denTarget == 0) return Double.NEGATIVE_INFINITY;

    // Return similarity
    double correlation = num / Math.sqrt(denActive * denTarget);
    return (correlation + 1.0) / 2.0;
  }
}
