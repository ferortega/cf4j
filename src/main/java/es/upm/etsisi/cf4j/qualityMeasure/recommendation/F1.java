package es.upm.etsisi.cf4j.qualityMeasure.recommendation;

import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.util.Search;

import java.util.Map;

/**
 * This class calculates the F1 score of the recommender system. F1 score is computed as follows:
 *
 * <p>F1 = 2 * precision * recall / (precision + recall)
 */
public class F1 extends QualityMeasure {

  /** Number of recommended items */
  private int numberOfRecommendations;

  /** Relevant rating threshold */
  private double relevantThreshold;

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
   * @param recommender Recommender instance for which the F1 are going to be computed
   * @param params Quality measure's parameters
   */
  public F1(Recommender recommender, Map<String, Object> params) {
    this(
        recommender,
        (int) params.get("numberOfRecommendations"),
        (double) params.get("relevantThreshold"));
  }

  /**
   * Constructor
   *
   * @param recommender Recommender instance for which the F1 are going to be computed
   * @param numberOfRecommendations Number of recommendations
   * @param relevantThreshold Minimum rating to consider a rating as relevant
   */
  public F1(Recommender recommender, int numberOfRecommendations, double relevantThreshold) {
    super(recommender);
    this.numberOfRecommendations = numberOfRecommendations;
    this.relevantThreshold = relevantThreshold;
  }

  @Override
  protected double getScore(TestUser testUser, double[] predictions) {

    // Items rated as relevant (in test) by the test user
    int relevant = 0;
    for (int pos = 0; pos < testUser.getNumberOfTestRatings(); pos++) {
      double rating = testUser.getTestRatingAt(pos);
      if (rating >= this.relevantThreshold) {
        relevant++;
      }
    }

    // Items that has been recommended and was relevant to the test user
    int[] recommendations = Search.findTopN(predictions, this.numberOfRecommendations);

    int recommendedAndRelevant = 0, recommended = 0;

    for (int pos : recommendations) {
      if (pos == -1) break;

      double rating = testUser.getTestRatingAt(pos);
      if (rating >= this.relevantThreshold) {
        recommendedAndRelevant++;
      }

      recommended++;
    }

    // Precision y Recall
    double precision = (double) recommendedAndRelevant / (double) recommended;
    double recall = (double) recommendedAndRelevant / (double) relevant;

    // F1 score
    return 2 * precision * recall / (precision + recall);
  }
}
