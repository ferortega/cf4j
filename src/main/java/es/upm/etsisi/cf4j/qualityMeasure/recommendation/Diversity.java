package es.upm.etsisi.cf4j.qualityMeasure.recommendation;

import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.recommender.knn.itemSimilarityMetric.Cosine;
import es.upm.etsisi.cf4j.recommender.knn.itemSimilarityMetric.ItemSimilarityMetric;
import es.upm.etsisi.cf4j.util.process.Parallelizer;
import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.util.Search;

import java.util.Map;

/**
 * This class the averaged diversity of the recomendations. Diversity value is computed as explained in "Hurley, N.,
 * &amp; Zhang, M. (2011). Novelty and diversity in top-n recommendation--analysis and evaluation. ACM Transactions on
 * Internet Technology (TOIT), 10(4), 1-30.". Lower values denotes more diverse recommendations.
 */
public class Diversity extends QualityMeasure {

	/**
	 * Number of recommended items
	 */
	private int numberOfRecommendations;

	/**
	 * Similarity between items
	 */
	ItemSimilarityMetric itemSimilarityMetric;

	/**
	 * Constructor from a Map object with the quality measure parameters. Map object must contains the
	 * following keys:
	 * <ul>
	 *   <li><b>numberOfRecommendations</b>: int value with the number of items to be recommended.</li>
	 * </ul>
	 * @param recommender Recommender instance for which the Diversity are going to be computed
	 * @param params Quality measure's parameters
	 */
	public Diversity(Recommender recommender, Map<String, Object> params) {
		this(recommender, (int) params.get("numberOfRecommendations"));
	}

	/**
	 * Constructor of Diversity
	 * @param recommender Recommender instance for which the precision are going to be computed
	 * @param numberOfRecommendations Number of recommendations
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

		int [] recommendations = Search.findTopN(predictions, this.numberOfRecommendations);
		
		double sum = 0;
		int count = 0;

		for (int i : recommendations) {
			if (i == -1) break;

			int iIndex = testUser.getTestItemAt(i);
			double[] similarities = this.itemSimilarityMetric.getSimilarities(iIndex);

			for (int j : recommendations) {
				if (j == -1) break;

				if (i != j) {
					int jIndex = testUser.getTestItemAt(j);
					double sim = similarities[jIndex];

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
