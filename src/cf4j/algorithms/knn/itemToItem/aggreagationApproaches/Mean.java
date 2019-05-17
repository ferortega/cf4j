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
 * <p>This class uses standard average as method to combine the ratings of the items more
 * similar to the active one.</p>
 * 
 * @author Fernando Ortega
 */
public class Mean extends TestPredictions {

	public Mean(DataModel dataModel) {
		super(dataModel);
	}

	@Override
	public double predict (TestUser testUser, String itemCode) {
		
		TestItem item = this.dataModel.getTestItem(itemCode);
		Integer [] neighbors = item.getStoredData().getIntegerArray(TestItem.NEIGHBORS_KEY);
		
		double prediction = 0;
		int count = 0;
		
		for (int n = 0; n < neighbors.length; n++) {
			if (neighbors[n] == -1) break; // Neighbors array are filled with -1 when no more neighbors exists
			
			int itemIndex = neighbors[n];
			Item neighbor = this.dataModel.getItemAt(itemIndex);
			String neighborCode = neighbor.getItemCode();
							
			int i = testUser.getItemIndex(neighborCode);
			if (i != -1) {
				prediction += testUser.getRatingAt(i);
				count++;
			}
		}
		
		if (count == 0) {
			return Double.NaN;
		} else {
			prediction /= count;
			return prediction;
		}
	}
}
