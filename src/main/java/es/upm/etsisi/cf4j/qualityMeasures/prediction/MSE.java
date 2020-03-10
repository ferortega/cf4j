package cf4j.qualityMeasures.prediction;

import cf4j.algorithms.Recommender;
import cf4j.data.TestUser;
import cf4j.qualityMeasures.QualityMeasure;


public class MSE extends QualityMeasure {

	public MSE(Recommender recommender) {
		super(recommender);
	}

	@Override
	public double getScore (TestUser testUser, double[] predictions) {

		double mse = 0d;
		int count = 0;
		
		for (int i = 0; i < testUser.getNumberOfTestRatings(); i++) {
			if (!Double.isNaN(predictions[i])) {
				double diff = predictions[i] -  testUser.getTestRatingAt(i);
				mse += diff * diff;
				count++;
			}
		}
		
		return (count == 0) ? Double.NaN : (mse / count);
	}
}
