package es.upm.etsisi.cf4j.qualityMeasures.prediction;


import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.qualityMeasures.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;

public class MSE extends QualityMeasure {

	public MSE(Recommender recommender) {
		super(recommender);
	}

	@Override
	public double getScore(TestUser testUser, double[] predictions) {

		double mse = 0d;
		int count = 0;
		
		for (int i = 0; i < testUser.getNumberOfTestRatings(); i++) {
			if (!Double.isNaN(predictions[i])) {
				double diff = predictions[i] -  testUser.getTestRating(i);
				mse += diff * diff;
				count++;
			}
		}
		
		return (count == 0) ? Double.NaN : (mse / count);
	}
}
