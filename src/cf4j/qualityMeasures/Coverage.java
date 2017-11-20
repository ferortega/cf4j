package cf4j.qualityMeasures;

import java.util.HashSet;

import cf4j.Kernel;
import cf4j.TestUser;
import cf4j.User;

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
		
		int [] neighbors = testUser.getNeighbors();
		
		// Get items rated by the knn
		HashSet <Integer> recommended = new HashSet <Integer> ();
		for (int n = 0; n < neighbors.length; n++) {
			if (neighbors[n] == -1) break;
			
			int userIndex = neighbors[n];
			User neighbor = Kernel.gi().getUsers()[userIndex];
			
			for (int itemCode : neighbor.getItems()) {
				recommended.add(itemCode);
			}
		}
		
		// Delete items rated by the user
		for (int itemCode : testUser.getItems()) {
			recommended.remove(itemCode);
		}
		
		double coverage = (double) recommended.size() / 
				(double) (Kernel.getInstance().getNumberOfItems() - testUser.getNumberOfRatings());
		
		return coverage;
	}
}
