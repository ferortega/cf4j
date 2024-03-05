package es.upm.etsisi.cf4j.metrics.recommendation;

import es.upm.etsisi.cf4j.data.TestItem;
import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.metrics.Score;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.util.Search;

import java.util.Map;

/**
 * This class the averaged novelty of the recomendations. Discovery value is computed as explained
 * in "Castells, P., Vargas, S., &amp; Wang, J. (2011). Novelty and diversity metrics for
 * recommender systems: choice, discovery and relevance.". Higher values denotes higher discovery.
 */
public class Discovery extends Score {

  /** Number of recommended items */
  private final int numberOfRecommendations;

  /**
   * Constructor from a Map object with the quality measure parameters. Map object must contains the
   * following keys:
   *
   * <ul>
   *   <li><b>numberOfRecommendations</b>: int value with the number of items to be recommended.
   * </ul>
   *
   * @param recommender Recommender instance for which the Discovery are going to be computed
   * @param params Quality measure's parameters
   */
  public Discovery(Recommender recommender, Map<String, Object> params) {
    this(recommender, (int) params.get("numberOfRecommendations"));
  }

  /**
   * Constructor of Novelty
   *
   * @param recommender Recommender instance for which the precision are going to be computed
   * @param numberOfRecommendations Number of recommendations
   */
  public Discovery(Recommender recommender, int numberOfRecommendations) {
    super(recommender);
    this.numberOfRecommendations = numberOfRecommendations;
  }

  @Override
  protected double getUserScore(TestUser testUser) {
    double[] predictions = recommender.predict(testUser);
    int[] recommendations = Search.findTopN(predictions, this.numberOfRecommendations);

    double sum = 0;
    int count = 0;

    for (int pos : recommendations) {
      if (pos == -1) break;

      int testItemIndex = testUser.getTestItemAt(pos);
      TestItem testItem = recommender.getDataModel().getTestItem(testItemIndex);

      double pi =
          (double) testItem.getNumberOfRatings()
              / (double) recommender.getDataModel().getNumberOfUsers();
      sum += 1 - pi;

      count++;
    }

    return (count == 0) ? Double.NaN : (sum / count);
  }
}
