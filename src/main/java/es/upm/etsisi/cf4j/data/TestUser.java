package es.upm.etsisi.cf4j.data;

import es.upm.etsisi.cf4j.data.types.SortedRatingList;

/**
 * A TestUser extends an User given him or her the following properties:
 *
 * <ul>
 *   <li>Index in the DataModel array which stores test users.
 *   <li>Array of items rated in test by the user.
 * </ul>
 *
 * <p>It is not recommended that developers generate new instances of this class since this is a
 * memory-structural class.
 */
public class TestUser extends User {

  private static final long serialVersionUID = 20200314L;

  /** Index in the DataModel array which stores test users */
  protected int testUserIndex;

  /** Minimum test rating value */
  protected double minTest = Double.MAX_VALUE;

  /** Maximum test rating value */
  protected double maxTest = Double.MIN_VALUE;

  /** Average test rating */
  protected double averageTest = 0.0;

  /** TestItems rated by the user */
  protected SortedRatingList testItemsRatings;

  /**
   * Creates a new instance of a test user. This constructor should not be used by developers.
   *
   * @param id User unique identifier
   * @param userIndex Index in the DataModel array which stores users
   * @param testUserIndex Index in the DataModel array which stores test users
   */
  public TestUser(String id, int userIndex, int testUserIndex) {
    super(id, userIndex);
    this.testUserIndex = testUserIndex;
    this.testItemsRatings = new SortedRatingList();
  }

  /**
   * Returns the test user index inside the DataModel
   *
   * @return testUserIndex inside the DataModel
   */
  public int getTestUserIndex() {
    return this.testUserIndex;
  }

  /**
   * Returns the index of the TestItem rated by the TestUser at the given position.
   *
   * @param pos Position
   * @return Index of the test item in the TestItems' array of the DataModel
   */
  public int getTestItemAt(int pos) {
    return this.testItemsRatings.get(pos).getIndex();
  }

  /**
   * Returns the test rating of the user to the test item at the pos position
   *
   * @param pos Position
   * @return Test rating at indicated position
   */
  public double getTestRatingAt(int pos) {
    return this.testItemsRatings.get(pos).getRating();
  }

  /**
   * Gets the number of test items rated by the user.
   *
   * @return Number of test ratings
   */
  public int getNumberOfTestRatings() {
    return this.testItemsRatings.size();
  }

  /**
   * Adds a new test rating of the test user to an test item. You cannot overwrite an existing
   * rating, otherwise this method will throws an IllegalArgumentException. It is not recommended to
   * use this method, use DataModel.addTestRating(...) instead.
   *
   * @param testItemIndex Test item index which identifies a test item in the DataModel
   * @param rating Rating value
   */
  public void addTestRating(int testItemIndex, double rating) {
    if (!this.testItemsRatings.add(testItemIndex, rating))
      throw new IllegalArgumentException("Provided rating already exist in user: " + id);

    minTest = Math.min(rating, minTest);
    maxTest = Math.max(rating, maxTest);
    averageTest =
        (this.testItemsRatings.size() <= 1)
            ? rating
            : ((averageTest * (this.testItemsRatings.size() - 1)) + rating)
                / this.testItemsRatings.size();
  }

  /**
   * Gets the minimum test rating of the user
   *
   * @return Minimum test rating
   */
  public double getMinTestRating() {
    return minTest;
  }

  /**
   * Gets the maximum test rating of the user
   *
   * @return Maximum test rating
   */
  public double getMaxTestRating() {
    return maxTest;
  }

  /**
   * Gets the average value of test ratings
   *
   * @return Test rating average
   */
  public double getTestRatingAverage() {
    return averageTest;
  }
}
