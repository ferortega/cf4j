package es.upm.etsisi.cf4j.metrics.recommendation;

import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.metrics.Score;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.util.Search;

import java.util.Map;

/**
 * This class calculates the recall of the recommendations performed by a Recommender. It is
 * calculated as follows:
 *
 * <p>recall = &lt;relevant recommended items&gt; / &lt;number of relevant items&gt;
 */
public class Recall extends Score {

  /** Number of recommended items */
  private final int numberOfRecommendations;

  /** Relevant rating threshold */
  private final double relevantThreshold;

  /**
   * Constructor from a Map object with the quality measure parameters. Map object must contains the
   * following keys:
   *
   * <ul>
   *   <li><b>numberOfRecommendations</b>: int value with the number of items to be recommended.
   *   <li><b>relevantThreshold:</b>: double value with the minimum rating to consider a test rating
   *       as relevant.
   * </ul>
   *
   * @param recommender Recommender instance for which the Recall are going to be computed
   * @param params Quality measure's parameters
   */
  public Recall(Recommender recommender, Map<String, Object> params) {
    this(
        recommender,
        (int) params.get("numberOfRecommendations"),
        (double) params.get("relevantThreshold"));
  }

  /**
   * Constructor
   *
   * @param recommender Recommender instance for which the recall are going to be computed
   * @param numberOfRecommendations Number of recommendations
   * @param relevantThreshold Minimum rating to consider a rating as relevant
   */
  public Recall(Recommender recommender, int numberOfRecommendations, double relevantThreshold) {
    super(recommender);
    this.numberOfRecommendations = numberOfRecommendations;
    this.relevantThreshold = relevantThreshold;
  }

  @Override
  protected double getUserScore(TestUser testUser) {
    double[] predictions = recommender.predict(testUser);

    // Items rated as relevant (in test) by the active user

    int relevant = 0;
    for (int i = 0; i < testUser.getNumberOfTestRatings(); i++) {
      double rating = testUser.getTestRatingAt(i);
      if (rating >= this.relevantThreshold) {
        relevant++;
      }
    }

    // Items that has been recommended and was relevant to the active user

    int[] recommendations = Search.findTopN(predictions, this.numberOfRecommendations);

    int recommendedAndRelevant = 0;

    for (int pos : recommendations) {
      if (pos == -1) break;

      double rating = testUser.getTestRatingAt(pos);
      if (rating >= this.relevantThreshold) {
        recommendedAndRelevant++;
      }
    }

    if (relevant == 0) return Double.NEGATIVE_INFINITY;
    return (double) recommendedAndRelevant / (double) relevant;
  }
}
