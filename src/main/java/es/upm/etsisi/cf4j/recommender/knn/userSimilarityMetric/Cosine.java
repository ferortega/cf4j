package es.upm.etsisi.cf4j.recommender.knn.userSimilarityMetric;

import es.upm.etsisi.cf4j.data.User;

/** Implements traditional Cosine as CF similarity metric. */
public class Cosine extends UserSimilarityMetric {

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
        num += user.getRatingAt(i) * otherUser.getRatingAt(j);
        denActive += user.getRatingAt(i) * user.getRatingAt(i);
        denTarget += otherUser.getRatingAt(j) * otherUser.getRatingAt(j);

        common++;
        i++;
        j++;
      }
    }

    // If there is not items in common, similarity does not exists
    if (common == 0) return Double.NEGATIVE_INFINITY;

    // Return similarity
    return num / (Math.sqrt(denActive) * Math.sqrt(denTarget));
  }
}
