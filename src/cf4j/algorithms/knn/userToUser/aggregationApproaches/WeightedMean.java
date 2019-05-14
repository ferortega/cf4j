package cf4j.algorithms.knn.userToUser.aggregationApproaches;

import cf4j.algorithms.TestPredictions;
import cf4j.data.DataModel;
import cf4j.data.TestUser;
import cf4j.data.User;

/**
 * <p>This class computes the prediction of the test users' test items. The results are 
 * saved in double array on the hashmap of each test user with the key "predictions". This 
 * array overlaps with the test items' array of the test users. For example, the prediction
 * retrieved with the method testUser.getPredictions()[i] is the prediction of the item
 * testUser.getTestItems()[i].</p>
 * 
 * <p>This class uses weighted average as method to combine the test user neighbors' 
 * ratings.</p>
 * 
 * @author Fernando Ortega
 */
public class WeightedMean extends TestPredictions {

	/**
	 * Minimum similarity computed
	 */
	private double minSim;
	
	/**
	 * Maximum similarity computed
	 */
	private double maxSim;

	public WeightedMean(DataModel dataModel) {
		super(dataModel);
	}

	@Override
	public void beforeRun() {
		super.beforeRun();
		this.maxSim = Double.MIN_VALUE;
		this.minSim = Double.MAX_VALUE;

		for (int i = 0; i < this.dataModel.getNumberOfTestUsers(); i++){
			TestUser testUser = this.dataModel.getTestUserByIndex(i);
			for (double m : testUser.getStoredData().getDoubleArray(TestUser.SIMILARITIES_KEY)) {
				if (!Double.isInfinite(m)) {
					if (m < this.minSim) this.minSim = m;
					if (m > this.maxSim) this.maxSim = m;
				}
			}
		}
	}

	/**
	 * Compute predictions using weighted average rating of neighbors.
	 * @param testUser User to get the prediction.
	 * @param itemCode Item to be predicted.
	 * @return Prediction value or Double.NaN if it can not be computed.
	 */
	public double predict(TestUser testUser, String itemCode) {
		
		Integer [] neighbors = testUser.getStoredData().getIntegerArray(TestUser.NEIGHBORS_KEY);
		Double [] similarities = testUser.getStoredData().getDoubleArray(TestUser.SIMILARITIES_KEY);
		
		double prediction = 0;
		double sumSimilarities = 0;
		
		for (int n = 0; n < neighbors.length; n++) {
			if (neighbors[n] == -1) break; // Neighbors array are filled with -1 when no more neighbors exists
			
			int userIndex = neighbors[n];
			User neighbor = this.dataModel.getUserByIndex(userIndex);
			
			int i = neighbor.getItemIndex(itemCode);
			if (i != -1) {
				double similarity = similarities[userIndex];
				double sim = (similarity - this.minSim) / (this.maxSim - this.minSim);
				
				double rating = neighbor.getRatingAt(i);

				prediction += sim * rating;
				sumSimilarities += sim;
			}
		}
		
		if (sumSimilarities == 0) {
			return Double.NaN;
		} else {
			prediction /= sumSimilarities;
			return prediction;
		}
	}
}
