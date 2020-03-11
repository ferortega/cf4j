package es.upm.etsisi.cf4j.qualityMeasures.prediction;

import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.qualityMeasures.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;

/**
 * <p>This class calculates the Coverage of the recommender system. The coverage is the capacity of
 * the recommender system to recommend new items. It is calculates as follows:</p>
 * 
 * <p>coverage = &lt;number of predicted items&gt; / &lt;number of items not rated by the user&gt;</p>
 * 
 * <p>This class puts the "Coverage" key at the Kernel map containing a double with the coverage 
 * value.</p>
 * 
 * @author Fernando Ortega
 */
public class Coverage extends QualityMeasure {

	/**
	 * Constructor of Coverage
	 */
	public Coverage(Recommender recommender) {
		super(recommender);
	}

	@Override
	public double getScore(TestUser testUser, double[] predictions) {
		int count = 0;
		
		for (int i = 0; i < predictions.length; i++) {
			double prediction = predictions[i];
			if (!Double.isNaN(prediction)) {
				count++;
			}
		}

		double coverage = (double) count / (double) testUser.getNumberOfTestRatings();
		
		return coverage;
	}
}
