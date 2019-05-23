package cf4j.algorithms.knn.itemToItem.aggreagationApproaches;

import cf4j.data.Item;
import cf4j.algorithms.TestPredictions;
import cf4j.data.DataModel;
import cf4j.data.TestItem;
import cf4j.data.TestUser;

/**
 * <p>This class computes the prediction of the test users' test items. The results are 
 * saved in double array on the hashmap of each test user with the key "predictions". This 
 * array overlaps with the test items' array of the test users. For example, the prediction
 * retrieved with the method testUser.getPredictions()[i] is the prediction of the item
 * testUser.getTestItems()[i].</p>
 * 
 * <p>This class uses weighted average as method to combine the ratings of the items more
 * similar to the active one.</p>
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
		
		for (int i = 0; i < dataModel.getNumberOfTestItems(); i++){
			TestItem testItem = dataModel.getTestItemAt(i);
			for (double m : testItem.getDataBank().getDoubleArray(TestItem.SIMILARITIES_KEY)){
				if (!Double.isInfinite(m)) {
					if (m < this.minSim) this.minSim = m;
					if (m > this.maxSim) this.maxSim = m;
				}
			}
		}
	}

	@Override
	public double predict (TestUser testUser, String itemCode) {
		
		TestItem testItem = dataModel.getTestItem(itemCode);
		
		Double [] similarities = testItem.getDataBank().getDoubleArray(TestItem.SIMILARITIES_KEY);
		Integer [] neighbors = testItem.getDataBank().getIntegerArray(TestItem.NEIGHBORS_KEY);
		
		double prediction = 0;
		double sum = 0;
		
		for (int n = 0; n < neighbors.length; n++) {
			if (neighbors[n] == -1) break; // Neighbors array are filled with -1 when no more neighbors exists
			
			int itemIndex = neighbors[n];
			Item neighbor = dataModel.getItemAt(itemIndex);
			String neighborCode = neighbor.getItemCode();
			
							
			int i = testUser.getItemIndex(neighborCode);
			if (i != -1) {
				double similarity = similarities[itemIndex];
				double sim = (similarity - this.minSim) / (this.maxSim - this.minSim);
				
				double rating = testUser.getRatingAt(i);
				
				prediction += sim * rating;
				sum += sim;
			}
		}
		
		if (sum == 0) {
			return Double.NaN;
		} else {
			prediction /= sum;
			return prediction;
		}
	}
}
