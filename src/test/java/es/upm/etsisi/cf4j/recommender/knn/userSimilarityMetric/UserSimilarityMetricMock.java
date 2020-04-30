package es.upm.etsisi.cf4j.recommender.knn.userSimilarityMetric;

import es.upm.etsisi.cf4j.data.User;

public class UserSimilarityMetricMock extends UserSimilarityMetric {

  @Override
  public double similarity(User user, User otherUser) {
    return 0.5;
  }
}
