package cf4j.qualityMeasures.prediction;

import cf4j.algorithms.Recommender;
import cf4j.data.DataModel;
import cf4j.data.TestUser;
import cf4j.qualityMeasures.QualityMeasure;

import java.util.ArrayList;


public class MAE extends QualityMeasure {

	public MAE (Recommender recommender) {
		super(recommender);
	}

	@Override
	public double getScore (TestUser testUser, double[] predictions) {

		double mae = 0d; 
		int count = 0;
		
		for (int i = 0; i < testUser.getNumberOfTestRatings(); i++) {
			if (!Double.isNaN(predictions[i])) {
				mae += Math.abs(predictions[i] - testUser.getTestRatingAt(i));
				count++;
			}
		}
		
		return (count == 0) ? Double.NaN : (mae / count);
	}
}
