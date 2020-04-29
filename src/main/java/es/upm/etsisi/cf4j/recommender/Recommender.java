package es.upm.etsisi.cf4j.recommender;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.TestItem;
import es.upm.etsisi.cf4j.data.TestUser;

/**
 * Abstract class that represents any recommender. To build a new recommender following methods must
 * be implemented:
 *
 * <ul>
 *   <li><i>fit()</i>: fits the recommender according to an instance of a DataModel.
 *   <li><i>predict(userIndex, itemIndex)</i>: computes a rating prediction for a given user and
 *       item.
 * </ul>
 */
public abstract class Recommender {

  /** DataModel instance used for the Recommender */
  protected DataModel datamodel;

  /**
   * Recommender constructor
   *
   * @param datamodel instance of a DataModel
   */
  protected Recommender(DataModel datamodel) {
    this.datamodel = datamodel;
  }

  /**
   * Returns the DataModel instance
   *
   * @return DataModel instance used by the Recommender
   */
  public DataModel getDataModel() {
    return this.datamodel;
  }

  /** Estimates model parameters given the hyper-parameters */
  public abstract void fit();

  /**
   * Computes a rating prediction
   *
   * @param userIndex Index of the user in the array of Users of the DataModel instance
   * @param itemIndex Index of the item in the array of Items of the DataModel instance
   * @return Prediction
   */
  public abstract double predict(int userIndex, int itemIndex);

  /**
   * Computes the rating predictions of the TestItems rated by a TestUser
   *
   * @param testUser TestUser for which to calculate resting predictions
   * @return Rating prediction for TestItems rated by testUser. Positions of this array overlaps
   *     with testItemIndexes returned by testUser.getTestItemAt(pos)
   */
  public double[] predict(TestUser testUser) {
    int userIndex = testUser.getUserIndex();
    double[] predictions = new double[testUser.getNumberOfTestRatings()];
    for (int i = 0; i < predictions.length; i++) {
      int testItemIndex = testUser.getTestItemAt(i);
      TestItem testItem = this.datamodel.getTestItem(testItemIndex);
      int itemIndex = testItem.getItemIndex();
      predictions[i] = this.predict(userIndex, itemIndex);
    }
    return predictions;
  }
}
