package es.upm.etsisi.cf4j.recommender.knn;

import es.upm.etsisi.cf4j.recommender.knn.itemSimilarityMetric.ItemSimilarityMetric;
import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.Item;
import es.upm.etsisi.cf4j.data.User;
import es.upm.etsisi.cf4j.util.process.Parallelizer;
import es.upm.etsisi.cf4j.util.process.Partible;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.util.Search;

import java.util.Map;

/** Implements item-to-item KNN based collaborative filtering */
public class ItemKNN extends Recommender {

  /** Available aggregation approaches to merge k-nearest neighbors ratings */
  public enum AggregationApproach {
    MEAN,
    WEIGHTED_MEAN
  }

  /** Number of neighbors (k) */
  protected int numberOfNeighbors;

  /** Similarity metric to compute the similarity between two items */
  protected ItemSimilarityMetric metric;

  /** Aggregation approach used to aggregate k-nearest neighbors ratings */
  protected AggregationApproach aggregationApproach;

  /** Contains the neighbors indexes of each item */
  protected int[][] neighbors;

  /**
   * Recommender constructor from a Map containing the recommender's hyper-parameters values. Map
   * object must contains the following keys:
   *
   * <ul>
   *   <li><b>numberOfNeighbors</b>: int value with the number of neighbors.
   *   <li><b>metric:</b>: ItemSimilarityMetric instance with the similarity metric to compute the
   *       similarity between two items.
   *   <li><b>aggregationApproach</b>: ItemKNN.AggregationApproach instance with the aggregation
   *       approach used to aggregate k-nearest neighbors ratings.
   * </ul>
   *
   * @param datamodel DataModel instance
   * @param params Recommender's hyper-parameters values
   */
  public ItemKNN(DataModel datamodel, Map<String, Object> params) {
    this(
        datamodel,
        (int) params.get("numberOfNeighbors"),
        (ItemSimilarityMetric) params.get("metric"),
        (ItemKNN.AggregationApproach) params.get("aggregationApproach"));
  }

  /**
   * Recommender constructor
   *
   * @param datamodel DataModel instance
   * @param numberOfNeighbors Number of neighbors (k)
   * @param metric Similarity metric to compute the similarity between two items
   * @param aggregationApproach Aggregation approach used to aggregate k-nearest neighbors ratings
   */
  public ItemKNN(
      DataModel datamodel,
      int numberOfNeighbors,
      ItemSimilarityMetric metric,
      AggregationApproach aggregationApproach) {
    super(datamodel);

    this.numberOfNeighbors = numberOfNeighbors;

    int numItems = this.datamodel.getNumberOfItems();
    this.neighbors = new int[numItems][numberOfNeighbors];

    this.metric = metric;
    this.metric.setDatamodel(this.datamodel);

    this.aggregationApproach = aggregationApproach;
  }

  @Override
  public void fit() {
    System.out.println("\nFitting " + this.toString());
    Parallelizer.exec(this.datamodel.getItems(), this.metric);
    Parallelizer.exec(this.datamodel.getItems(), new ItemNeighbors());
  }

  @Override
  public double predict(int userIndex, int itemIndex) {
    switch (this.aggregationApproach) {
      case MEAN:
        return predictMean(userIndex, itemIndex);
      case WEIGHTED_MEAN:
        return predictWeightedMean(userIndex, itemIndex);
      default:
        return Double.NaN;
    }
  }

  /**
   * Implementation of MEAN aggregation approach
   *
   * @param userIndex user index
   * @param itemIndex item index
   * @return Ration prediction from the user to the item
   */
  private double predictMean(int userIndex, int itemIndex) {
    User user = this.datamodel.getUser(userIndex);

    double prediction = 0;
    int count = 0;

    for (int neighborIndex : this.neighbors[itemIndex]) {
      if (neighborIndex == -1)
        break; // Neighbors array are filled with -1 when no more neighbors exists

      int pos = user.findItem(neighborIndex);
      if (pos != -1) {
        prediction += user.getRatingAt(pos);
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

  /**
   * Implementation of WEIGHTED_MEAN aggregation approach
   *
   * @param userIndex user index
   * @param itemIndex item index
   * @return Ration prediction from the user to the item
   */
  private double predictWeightedMean(int userIndex, int itemIndex) {
    User user = this.datamodel.getUser(userIndex);

    double[] similarities = metric.getSimilarities(itemIndex);

    double num = 0;
    double den = 0;

    for (int neighborIndex : this.neighbors[itemIndex]) {
      if (neighborIndex == -1)
        break; // Neighbors array are filled with -1 when no more neighbors exists

      int pos = user.findItem(neighborIndex);
      if (pos != -1) {
        double similarity = similarities[neighborIndex];
        double rating = user.getRatingAt(pos);
        num += similarity * rating;
        den += similarity;
      }
    }

    return (den == 0) ? Double.NaN : num / den;
  }

  @Override
  public String toString() {
    StringBuilder str =
        new StringBuilder("ItemKNN(")
            .append("numberOfNeighbors=")
            .append(this.numberOfNeighbors)
            .append("; ")
            .append("metric=")
            .append(this.metric.getClass().getSimpleName())
            .append("; ")
            .append("aggregationApproach=")
            .append(this.aggregationApproach)
            .append(")");
    return str.toString();
  }

  /** Private class to parallelize neighbors computation */
  private class ItemNeighbors implements Partible<Item> {

    @Override
    public void beforeRun() {}

    @Override
    public void run(Item item) {
      int itemIndex = item.getItemIndex();
      double[] similarities = metric.getSimilarities(itemIndex);
      neighbors[itemIndex] = Search.findTopN(similarities, numberOfNeighbors);
    }

    @Override
    public void afterRun() {}
  }
}
