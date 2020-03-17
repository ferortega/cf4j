package es.upm.etsisi.cf4j.qualityMeasures.prediction;


import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.qualityMeasures.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;

public class MAE extends QualityMeasure {

	public MAE(Recommender recommender) {
		super(recommender);
	}

	@Override
	public double getScore(TestUser testUser, double[] predictions) {

		double mae = 0d; 
		int count = 0;
		
		for (int i = 0; i < testUser.getNumberOfTestRatings(); i++) {
			if (!Double.isNaN(predictions[i])) {
				mae += Math.abs(predictions[i] - testUser.getTestRating(i));
				count++;
			}
		}
		
		return (count == 0) ? Double.NaN : (mae / count);
	}
}
