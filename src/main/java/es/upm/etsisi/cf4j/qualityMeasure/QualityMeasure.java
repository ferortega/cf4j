package es.upm.etsisi.cf4j.qualityMeasure;

import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.process.Parallelizer;
import es.upm.etsisi.cf4j.process.Partible;
import es.upm.etsisi.cf4j.recommender.Recommender;

/**
 * Abstract class used to simplify the evaluation of collaborative filtering based recommendation models. To
 * define a new quality measure, getScore(TestUser testUser, double[] predictions) must be encoded.
 */
public abstract class QualityMeasure {

	/**
	 * Recommender instance for which the quality measure are going to be computed
	 */
	private Recommender recommender;

	/**
	 * Stores de global score of the quality measures
	 */
	private double score;

	/**
	 * Stores the score of each test user
	 */
	private double[] usersScores;

	/**
	 * Creates a new quality measure
	 * @param recommender Recommender instance for which the quality measure are going to be computed
	 */
	public QualityMeasure(Recommender recommender) {
		this.recommender = recommender;
	}

	/**
	 * Computes the quality measure score for a TestUser given the predictions for his/her test ratings
	 * @param testUser TestUser for which the quality measure score is computed
	 * @param predictions Prediction value for the test items rated by the test user
	 * @return Quality measure score
	 */
	protected abstract double getScore(TestUser testUser, double[] predictions);

	/**
	 * Computes the quality measure of the recommender
	 * @return Quality measure score
	 */
	public double getScore() {
		Parallelizer.exec(recommender.getDataModel().getTestUsers(), new EvaluateUsers());
		return score;
	}

	/**
	 * Private inner class used to parallelize the computation of the quality measures
	 */
	private class EvaluateUsers implements Partible<TestUser> {

		@Override
		public void beforeRun() {
			usersScores = new double[recommender.getDataModel().getNumberOfTestUsers()];
		}

		@Override
		public void run(TestUser testUser) {
			int testUserIndex = testUser.getTestUserIndex();
			double[] predictions = recommender.predict(testUser);
			usersScores[testUserIndex] = QualityMeasure.this.getScore(testUser, predictions);
		}

		@Override
		public void afterRun() {
			double sum = 0;
			int count = 0;
			for (double us : usersScores) {
				if (!Double.isNaN(us)) {
					sum += us;
					count++;
				}
			}
			score = sum / count;
		}
	}
}
