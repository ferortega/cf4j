package es.upm.etsisi.cf4j.data;

import es.upm.etsisi.cf4j.data.types.SortedRatingList;

/**
 * A TestItem extends an Item given it the following properties:
 *
 * <ul>
 *   <li>Index in the DataModel array which stores test items.
 *   <li>Array of test ratings made by the test users.
 * </ul>
 *
 * <p>It is not recommended that developers generate new instances of this class since this is a
 * memory-structural class.
 */
public class TestItem extends Item {

  private static final long serialVersionUID = 20200314L;

  /** Index in the DataModel array which stores test items */
  protected int testItemIndex;

  /** Minimum test rating value */
  protected double minTest = Double.MAX_VALUE;

  /** Maximum test rating value */
  protected double maxTest = Double.MIN_VALUE;

  /** Average test rating */
  protected double averageTest = 0.0;

  /** Array of test users that have rated this test item */
  protected SortedRatingList testUsersRatings;

  /**
   * Creates a new instance of a test item. This constructor should not be used by developers.
   *
   * @param id Item unique identifier
   * @param itemIndex Index in the DataModel array which stores items
   * @param testItemIndex Index in the DataModel array which stores test items
   */
  public TestItem(String id, int itemIndex, int testItemIndex) {
    super(id, itemIndex);
    this.testItemIndex = testItemIndex;
    this.testUsersRatings = new SortedRatingList();
  }

  /**
   * Returns the test item index inside the DataModel
   *
   * @return testItemIndex inside the DataModel
   */
  public int getTestItemIndex() {
    return this.testItemIndex;
  }

  /**
   * Returns the index of the TestUser that have test rated the TestItem at the given position
   *
   * @param pos Position
   * @return Index of the test user in the TestUsers' array of the DataModel
   */
  public int getTestUserAt(int pos) {
    return this.testUsersRatings.get(pos).getIndex();
  }

  /**
   * Returns the test rating of the test user to the test item at the pos position
   *
   * @param pos Position
   * @return Test rating at indicated position
   */
  public double getTestRatingAt(int pos) {
    return this.testUsersRatings.get(pos).getRating();
  }

  /**
   * Gets the number of test users that have rated the item.
   *
   * @return Number of test ratings
   */
  public int getNumberOfTestRatings() {
    return this.testUsersRatings.size();
  }

  /**
   * Adds a new test rating of a test user to the test item. You cannot overwrite an existing
   * rating, otherwise this method will throws an IllegalArgumentException. It is not recommended to
   * use this method, use DataModel.addTestRating(...) instead.
   *
   * @param testUserIndex Test user index which identifies the specific test user in the DataModel
   * @param rating Rating value
   */
  public void addTestRating(int testUserIndex, double rating) {
    if (!this.testUsersRatings.add(testUserIndex, rating))
      throw new IllegalArgumentException("Provided rating already exist in test item: " + id);

    minTest = Math.min(rating, minTest);
    maxTest = Math.max(rating, maxTest);
    averageTest =
        (this.testUsersRatings.size() <= 1)
            ? rating
            : ((averageTest * (this.testUsersRatings.size() - 1)) + rating)
                / this.testUsersRatings.size();
  }

  /**
   * Gets the minimum test rating received by the item
   *
   * @return Minimum test rating
   */
  public double getMinTestRating() {
    return minTest;
  }

  /**
   * Gets the maximum test rating received by the item
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
