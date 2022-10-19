package es.upm.etsisi.cf4j.qualityMeasure.recommendation;

import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.util.Search;

import java.util.Map;

/**
 * This class calculates the Normalized Discounted Cumulative Gain (nDCG) of the recommendations
 * performed by a Recommender. It is calculated as follows:
 *
 * <p>NDCG = &lt;DCG&gt; / &lt;IDCG&gt;
 */
public class NDCG extends QualityMeasure {

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
   * @param recommender Recommender instance for which the NDCG are going to be computed
   * @param params Quality measure's parameters
   */
  public NDCG(Recommender recommender, Map<String, Object> params) {
    this(recommender, (int) params.get("numberOfRecommendations"));
  }

  /**
   * Constructor
   *
   * @param recommender Recommender instance for which the nDCG are going to be computed
   * @param numberOfRecommendations Number of recommendations
   */
  public NDCG(Recommender recommender, int numberOfRecommendations) {
    super(recommender);
    this.numberOfRecommendations = numberOfRecommendations;
  }

  @Override
  protected double getScore(TestUser testUser, double[] predictions) {

    // Compute DCG

    int[] recommendations = Search.findTopN(predictions, this.numberOfRecommendations);

    double dcg = dataCalculation(testUser,recommendations);

    // Compute IDCG

    double[] testRatings = new double[testUser.getNumberOfTestRatings()];
    for (int pos = 0; pos < testRatings.length; pos++) {
      testRatings[pos] = testUser.getTestRatingAt(pos);
    }

    int[] idealRecommendations = Search.findTopN(testRatings, this.numberOfRecommendations);

    double idcg = dataCalculation(testUser,idealRecommendations);

    if (idcg == 0) return Double.NEGATIVE_INFINITY;
    // Compute NDCG
    return dcg / idcg;
  }

  /**
   * Function to process de data in the NDCG algorithm. Extracted in order to dont repeat code.
   *
   * @param testUser Related used
   * @param elements Recomendations or ideal recommendations
   *
   * @return discounted cumulative gain
   */
  protected double dataCalculation(TestUser testUser, int[] elements){
    double result = 0d;

    for (int i = 0; i < elements.length; i++) {
      int pos = elements[i];
      if (pos == -1) break;

      double rating = testUser.getTestRatingAt(pos);
      result += (Math.pow(2, rating) - 1) / (Math.log(i + 2) / Math.log(2));
    }

    return result;
  }
}
