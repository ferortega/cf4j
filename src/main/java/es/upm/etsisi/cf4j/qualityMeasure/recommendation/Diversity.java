package es.upm.etsisi.cf4j.qualityMeasure.recommendation;

import es.upm.etsisi.cf4j.data.TestItem;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.recommender.knn.itemSimilarityMetric.Cosine;
import es.upm.etsisi.cf4j.recommender.knn.itemSimilarityMetric.ItemSimilarityMetric;
import es.upm.etsisi.cf4j.util.process.Parallelizer;
import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.util.Search;

import java.util.Map;

/**
 * This class the averaged diversity of the recomendations. Diversity value is computed as explained
 * in "Hurley, N., &amp; Zhang, M. (2011). Novelty and diversity in top-n recommendation--analysis
 * and evaluation. ACM Transactions on Internet Technology (TOIT), 10(4), 1-30.". Lower values
 * denotes more diverse recommendations.
 */
public class Diversity extends QualityMeasure {

  /** Number of recommended items */
  private int numberOfRecommendations;

  /** Similarity between items */
  ItemSimilarityMetric itemSimilarityMetric;

  /**
   * Constructor from a Map object with the quality measure parameters. Map object must contains the
   * following keys:
   *
   * <ul>
   *   <li><b>numberOfRecommendations</b>: int value with the number of items to be recommended.
   * </ul>
   *
   * @param recommender Recommender instance for which the Diversity are going to be computed
   * @param params Quality measure's parameters
   */
  public Diversity(Recommender recommender, Map<String, Object> params) {
    this(recommender, (int) params.get("numberOfRecommendations"));
  }

  /**
   * Constructor of Diversity
   *
   * @param recommender Recommender instance for which the precision are going to be computed
   * @param numberOfRecommendations Number of recommendations. It must be greater than 1
   */
  public Diversity(Recommender recommender, int numberOfRecommendations) {
    super(recommender);
    this.numberOfRecommendations = numberOfRecommendations;

    // Compute similarity between items
    this.itemSimilarityMetric = new Cosine();
    this.itemSimilarityMetric.setDatamodel(super.recommender.getDataModel());
    Parallelizer.exec(recommender.getDataModel().getItems(), this.itemSimilarityMetric);
  }

  @Override
  protected double getScore(TestUser testUser, double[] predictions) {

    int[] recommendations = Search.findTopN(predictions, this.numberOfRecommendations);

    double sum = 0;
    int count = 0;

    for (int iPos : recommendations) {
      if (iPos == -1) break;

      int iTestItemIndex = testUser.getTestItemAt(iPos);
      int iItemIndex = super.recommender.getDataModel().getTestItem(iTestItemIndex).getItemIndex();

      double[] similarities = this.itemSimilarityMetric.getSimilarities(iItemIndex);

      for (int jPos : recommendations) {
        if (jPos == -1) break;

        if (iPos != jPos) {
          int jTestItemIndex = testUser.getTestItemAt(jPos);
          int jItemIndex = super.recommender.getDataModel().getTestItem(jTestItemIndex).getItemIndex();

          double sim = similarities[jItemIndex];

          // Ignore items without common ratings (sim == Double.NEGATIVE_INFINITY)
          if (!Double.isInfinite(sim)) {
            sum += sim;
            count++;
          }
        }
      }
    }

    return (count == 0) ? Double.NaN : (sum / count);
  }
}
