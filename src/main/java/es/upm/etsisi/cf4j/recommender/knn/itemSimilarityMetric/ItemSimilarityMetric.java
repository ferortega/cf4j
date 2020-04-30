package es.upm.etsisi.cf4j.recommender.knn.itemSimilarityMetric;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.Item;
import es.upm.etsisi.cf4j.util.process.Partible;

/**
 * This class process the similarity measure between two items. To define your own similarity metric
 * implementation, you must extend this class and overrides the method similarity(Item item, Item
 * otherItem).
 *
 * <p>When the execution of the similarity metric is completed, the similarity of each item with
 * respect to another one can be retrieved using the getSimilarities(int itemIndex) method.
 */
public abstract class ItemSimilarityMetric implements Partible<Item> {

  /** DataModel for which de similarities must be computed */
  protected DataModel datamodel;

  /** Matrix that contains the similarity between each pair of items */
  protected double[][] similarities;

  /**
   * Sets the DataModel for which the similarity are going to be computed
   *
   * @param datamodel DataModel instance
   */
  public void setDatamodel(DataModel datamodel) {
    this.datamodel = datamodel;
    this.similarities = new double[datamodel.getNumberOfItems()][datamodel.getNumberOfItems()];
  }

  /**
   * Returns the similarity array of an item. Each position of the array contains the similarity of
   * the item with the corresponding item at the same position in the array of Items of the
   * DataModel instance.
   *
   * @param itemIndex Index of the item
   * @return Similarity of an item with other items of the DataModel instance
   */
  public double[] getSimilarities(int itemIndex) {
    return this.similarities[itemIndex];
  }

  /**
   * This method must returns the similarity between two items.
   *
   * <p>If two items do not have a similarity value, the method must return
   * Double.NEGATIVE_INFINITY.
   *
   * <p>The value returned by this method should be higher the greater the similarity between items.
   *
   * @param item An item
   * @param otherItem Other item
   * @return Similarity between item and otherItem
   */
  public abstract double similarity(Item item, Item otherItem);

  @Override
  public void beforeRun() {}

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
  public void afterRun() {}

  @Override
  public String toString() {
    return this.getClass().getSimpleName();
  }
}
