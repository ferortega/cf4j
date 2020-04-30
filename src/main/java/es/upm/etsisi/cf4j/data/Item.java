package es.upm.etsisi.cf4j.data;

import java.io.Serializable;

import es.upm.etsisi.cf4j.data.types.SortedRatingList;

/**
 * Defines a composition of an Item. An item is composed by:
 *
 * <ul>
 *   <li>Item unique identifier.
 *   <li>Item index in the DataModel which stores it.
 *   <li>Array of ratings made by users.
 * </ul>
 *
 * <p>It is not recommended that developers generate new instances of this class since this is a
 * memory-structural class.
 */
public class Item implements Serializable {

  private static final long serialVersionUID = 20200314L;

  /** Item unique identifier */
  protected String id;

  /** Item index in the DataModel */
  protected int itemIndex;

  /** Minimum (training) rating value */
  protected double min = Double.MAX_VALUE;

  /** Maximum (training) rating value */
  protected double max = Double.MIN_VALUE;

  /** Average (training) rating */
  protected double average = 0.0;

  /** DataBank to store heterogeneous information */
  protected DataBank dataBank;

  /** Users that have rated the item */
  protected SortedRatingList usersRatings;

  /**
   * Creates a new instance of an item. This constructor should not be used by developers.
   *
   * @param id Item unique identifier
   * @param index Index of the item in the Items' array of the DataModel
   */
  public Item(String id, int index) {
    this.id = id;
    this.itemIndex = index;
    this.dataBank = new DataBank();
    this.usersRatings = new SortedRatingList();
  }

  /**
   * Gets the DataBank instance that stores heterogeneous information related to the Item.
   *
   * @return DataBank instance
   */
  public DataBank getDataBank() {
    return dataBank;
  }

  /**
   * Returns the item unique identifier
   *
   * @return Item identifier
   */
  public String getId() {
    return this.id;
  }

  /**
   * Return the item index inside the DataModel
   *
   * @return Item index inside the DataModel
   */
  public int getItemIndex() {
    return this.itemIndex;
  }

  /**
   * Returns the index of the User that have rated the Item at the given position
   *
   * @param pos Position
   * @return Index of the user in the Users' array of the DataModel
   */
  public int getUserAt(int pos) {
    return this.usersRatings.get(pos).getIndex();
  }

  /**
   * Returns the rating of the user to the item at the pos position
   *
   * @param pos Position
   * @return Rating at indicated position
   */
  public double getRatingAt(int pos) {
    return this.usersRatings.get(pos).getRating();
  }

  /**
   * Finds position of a rating that an user has made to the item given the index of the User in the
   * DataModel.
   *
   * @param userIndex User index
   * @return User position if the item has been rated by the user or -1 if do not
   */
  public int findUser(int userIndex) {
    return this.usersRatings.find(userIndex);
  }

  /**
   * Gets the number of ratings that the item have received
   *
   * @return Number of ratings
   */
  public int getNumberOfRatings() {
    return this.usersRatings.size();
  }

  /**
   * Adds a new rating of an user to the item. You cannot overwrite an existing rating, otherwise
   * this method will throws an IllegalArgumentException. It is not recommended to use this method,
   * use DataModel.addRating(...) instead.
   *
   * @param userIndex User index which identifies the specific user in the DataModel
   * @param rating Rating value
   */
  public void addRating(int userIndex, double rating) {
    if (!this.usersRatings.add(userIndex, rating))
      throw new IllegalArgumentException("Provided rating already exist in item: " + id);

    min = Math.min(rating, min);
    max = Math.max(rating, max);
    average =
        (this.usersRatings.size() <= 1)
            ? rating
            : ((average * (this.usersRatings.size() - 1)) + rating) / this.usersRatings.size();
  }

  /**
   * Gets the minimum rating received by the item.
   *
   * @return Minimum rating
   */
  public double getMinRating() {
    return min;
  }

  /**
   * Gets the maximum rating received by the item.
   *
   * @return Maximum rating
   */
  public double getMaxRating() {
    return max;
  }

  /**
   * Gets the average value of ratings
   *
   * @return Rating average
   */
  public double getRatingAverage() {
    return average;
  }
}
