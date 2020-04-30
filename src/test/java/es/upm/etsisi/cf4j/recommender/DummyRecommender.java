package es.upm.etsisi.cf4j.recommender;

import es.upm.etsisi.cf4j.data.DataModel;

public class DummyRecommender extends Recommender {

  /**
   * Recommender constructor
   *
   * @param datamodel instance of a DataModel
   */
  public DummyRecommender(DataModel datamodel) {
    super(datamodel); // Passing through...
  }

  @Override
  public void fit() {
    // Nothing
  }

  @Override
  public double predict(int userIndex, int itemIndex) {
    return 0; // For example
  }
}
