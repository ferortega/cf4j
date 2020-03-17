package es.upm.etsisi.cf4j.recommender.knn.itemToItemMetrics;


import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.Item;
import es.upm.etsisi.cf4j.process.Partible;

/**
 * @author Fernando Ortega
 */
public abstract class ItemToItemMetric implements Partible<Item> {

	protected DataModel datamodel;
	protected double[][] similarities;

	public ItemToItemMetric(DataModel datamodel, double[][] similarities) {
		this.datamodel = datamodel;
		this.similarities = similarities;
	}


	abstract public double similarity(Item item, Item otherItem);

	@Override
	public void beforeRun() { }

	@Override
	public void run(Item item) {
		int itemIndex = item.getItemIndex();

		for (int i = 0; i < this.datamodel.getNumberOfItems(); i++) {
			Item otherItem = this.datamodel.getItem(i);
			if (itemIndex == otherItem.getItemIndex()) {
				similarities[itemIndex][i] = Double.NEGATIVE_INFINITY;
			} else {
				similarities[itemIndex][i] = this.similarity(item, otherItem);
			}
		}
	}

	@Override
	public void afterRun() { }
}
