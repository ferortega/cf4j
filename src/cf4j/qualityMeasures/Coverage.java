package cf4j.qualityMeasures;

import cf4j.TestUser;

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

	private final static String NAME = "Coverage";

	/**
	 * Constructor of Coverage
	 */
	public Coverage () {
		super(NAME);
	}

	@Override
	public double getMeasure (TestUser testUser) {
		
		double [] predictions = testUser.getPredictions();
		
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
