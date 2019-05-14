package cf4j.algorithms.knn.itemToItem.similarities;

import cf4j.data.Item;
import cf4j.data.DataModel;
import cf4j.data.TestItem;
import cf4j.process.PartibleThreads;

/**
 * <p>This class process the similarity measure between two items. If you want to define your own similarity
 * metric implementation, you must extend this class and implements the abstract method similarity (...).</p>
 * 
 * <p>When the execution of the similarity metric is complete, all the test items will have a double array
 * on his map saved with the key "similarities". The similarities can be retrieved using getSimilarities() 
 * method of TestItem class. The positions of this array overlaps with the array returned by the method 
 * getItems() of the Kernel class. For example, testItem.getSimilarities()[i] will contains the similarity 
 * between testItem and Kernel.getInstance().getItems()[i] item.</p>
 * 
 * @author Fernando Ortega
 */
abstract public class ItemSimilarities extends PartibleThreads {

	public ItemSimilarities(DataModel dataModel) {
		super(dataModel);
	}

	/**
	 * <p>Method to calculate the similarity measure between a pair of items.</p>
	 * <p>If not able to calculate the similarity measure between two items Double.NEGATIVE_INIFINITY 
	 * is returned.</p>
	 * <p>The similarity measure must be greater the more similar are the items.</p>
	 * @param activeItem Active item
	 * @param targetItem Item with which the similarity is computed
	 * @return Similarity measure between the two items
	 */
	abstract public double similarity (TestItem activeItem, Item targetItem);

	@Override
	public int getTotalIndexes () { return dataModel.getNumberOfTestItems(); }

	@Override
	public void beforeRun () { }

	@Override
	public void run (int testItemIndex) {
		
		TestItem activeItem = dataModel.getTestItemByIndex(testItemIndex);
		
		int numItems = dataModel.getNumberOfItems();
		Double [] similarities = new Double [numItems];
		
		for (int i = 0; i < numItems; i++) {
			Item targetItem = dataModel.getItemByIndex(i);
			if (activeItem.getItemCode() == targetItem.getItemCode()) {
				similarities[i] = Double.NEGATIVE_INFINITY;
			} else { 
				similarities[i] = this.similarity(activeItem, targetItem);
			}
		}

		activeItem.getStoredData().setDoubleArray(TestItem.SIMILARITIES_KEY,similarities);
	}
	
	@Override
	public void afterRun () { }
}
