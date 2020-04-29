package es.upm.etsisi.cf4j.recommender.knn.userSimilarityMetric;

import es.upm.etsisi.cf4j.data.User;

/** Implements traditional Jaccard Index as CF similarity metric. */
public class Jaccard extends UserSimilarityMetric {

  @Override
  public double similarity(User user, User otherUser) {

    int i = 0, j = 0, common = 0;
    while (i < user.getNumberOfRatings() && j < otherUser.getNumberOfRatings()) {
      if (user.getItemAt(i) < otherUser.getItemAt(j)) {
        i++;
      } else if (user.getItemAt(i) > otherUser.getItemAt(j)) {
        j++;
      } else {
        common++;
        i++;
        j++;
      }
    }

    // If there is not items in common, similarity does not exists
    if (common == 0) return Double.NEGATIVE_INFINITY;

    // Return similarity
    return (double) common
        / (double) (user.getNumberOfRatings() + otherUser.getNumberOfRatings() - common);
  }
}
