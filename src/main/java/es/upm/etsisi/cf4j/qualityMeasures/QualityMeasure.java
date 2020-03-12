package es.upm.etsisi.cf4j.qualityMeasures;


import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.process.Parallelizer;
import es.upm.etsisi.cf4j.process.Partible;
import es.upm.etsisi.cf4j.recommender.Recommender;

public abstract class QualityMeasure {

	private double score;

	private double[] usersScores;

	private Recommender recommender;

	public QualityMeasure(Recommender recommender) {
		this.recommender = recommender;
	}

	protected abstract double getScore(TestUser testUser, double[] predictions);

	public double getScore() {
		Parallelizer.exec(recommender.getDataModel().getTestUsers(), new EvaluateUsers());
		return score;
	}

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
