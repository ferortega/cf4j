package es.upm.etsisi.cf4j.qualityMeasures.recommendation;

import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.qualityMeasures.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.utils.Methods;

/**
 * <p>This class calculates the Normalized Discounted Cumulative Gain (nDCG) of the recommendations performed by a
 * Recommender. It is calculated as follows:</p>
 * <p>NDCG = &lt;SumDcg&gt; / &lt;SumIdcg&gt;</p>
 * @author Bo Zhu
 */
public class Ndcg extends QualityMeasure {

	/**
	 * Number of recommended items
	 */
	private int numberOfRecommendations;

	/**
	 * Constructor
	 * @param recommender Recommender instance for which the nDCG are going to be computed
	 * @param numberOfRecommendations Number of recommendations
	 */
	public Ndcg(Recommender recommender, int numberOfRecommendations) {
		super(recommender);
		this.numberOfRecommendations = numberOfRecommendations;
	}

	@Override
	protected double getScore(TestUser testUser, double[] predictions) {

		// Compute DCG

		int [] recommendations = Methods.findTopN(predictions, this.numberOfRecommendations);

		double dcg = 0d;

		for (int pos = 0; pos < recommendations.length; pos++) {
			int i = recommendations[pos];
			if (i == -1) break;

			double rating = testUser.getTestRatingAt(i);
			dcg += (Math.pow(2, rating) - 1) / (Math.log(pos + 2) / Math.log(2));
		}

		// Compute IDCG

		double[] testRatings = new double[testUser.getNumberOfTestRatings()];
		for (int i = 0; i < testRatings.length; i++) {
			testRatings[i] = testUser.getRatingAt(i);
		}

		int [] idealRecommendations = Methods.findTopN(testRatings, this.numberOfRecommendations);

		double idcg = 0d;

		for (int pos = 0; pos < idealRecommendations.length; pos++) {
			int i = idealRecommendations[pos];
			if (i == -1) break;

			double rating = testUser.getTestRatingAt(i);
			idcg += (Math.pow(2, rating) - 1) / (Math.log(pos + 2) / Math.log(2));
		}

		// Compute NDCG

		double ndcg = dcg / idcg;

		return ndcg;
	}
}