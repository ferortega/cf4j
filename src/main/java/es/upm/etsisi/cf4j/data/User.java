package es.upm.etsisi.cf4j.data;

import java.io.Serializable;

import es.upm.etsisi.cf4j.data.types.SortedRatingList;

/**
 * Defines a composition of an User. An user is composed by:
 *
 * <ul>
 *   <li>User unique identifier.
 *   <li>User index in the DataModel which stores him or her.
 *   <li>Array of items rated by the user.
 * </ul>
 *
 * <p>It is not recommended that developers generate new instances of this class since this is a
 * memory-structural class.
 */
public class User implements Serializable {

  private static final long serialVersionUID = 20200314L;

  /** User unique identifier */
  protected String id;

  /** User index in the DataModel */
  protected int userIndex;

  /** Minimum (training) rating value */
  protected double min = Double.MAX_VALUE;

  /** Maximum (training) rating value */
  protected double max = Double.MIN_VALUE;

  /** Average (training) rating */
  protected double average = 0.0;

  /** DataBank to store heterogeneous information */
  protected DataBank dataBank;

  /** Items rated by the user */
  protected SortedRatingList itemsRatings;

  /**
   * Creates a new instance of an user. This constructor should not be used by developers.
   *
   * @param id User unique identifier
   * @param index Index of the user in the Users' array of the DataModel
   */
  public User(String id, int index) {
    this.id = id;
    this.userIndex = index;
    this.dataBank = new DataBank();
    this.itemsRatings = new SortedRatingList();
  }

  /**
   * Gets the DataBank instance that stores heterogeneous information related to the User.
   *
   * @return DataBank instance
   */
  public DataBank getDataBank() {
    return dataBank;
  }

  /**
   * Returns the user unique identifier
   *
   * @return User identifier
   */
  public String getId() {
    return this.id;
  }

  /**
   * Return the user index inside the DataModel
   *
   * @return User index inside the DataModel
   */
  public int getUserIndex() {
    return this.userIndex;
  }

  /**
   * Returns the index of the Item rated by the User at the given position.
   *
   * @param pos Position
   * @return Index of the item in the Items' array of the DataModel
   */
  public int getItemAt(int pos) {
    return this.itemsRatings.get(pos).getIndex();
  }

  /**
   * Returns the rating of the user to the item at the pos position
   *
   * @param pos Position
   * @return Rating at indicated position
   */
  public double getRatingAt(int pos) {
    return this.itemsRatings.get(pos).getRating();
  }

  /**
   * Finds position of a user's rating given the index of the Item in the DataModel.
   *
   * @param itemIndex Item index
   * @return Item position if the item has been rated by the user or -1 if do not
   */
  public int findItem(int itemIndex) {
    return itemsRatings.find(itemIndex);
  }

  /**
   * Gets the number of items rated by the user
   *
   * @return Number of ratings
   */
  public int getNumberOfRatings() {
    return this.itemsRatings.size();
  }

  /**
   * Adds a new rating of the user to an item. You cannot overwrite an existing rating, otherwise
   * this method will throws an IllegalArgumentException. It is not recommended to use this method,
   * use DataModel.addRating(...) instead.
   *
   * @param itemIndex Item index which identifies an item in the DataModel
   * @param rating Rating value
   */
  public void addRating(int itemIndex, double rating) {
    if (!this.itemsRatings.add(itemIndex, rating))
      throw new IllegalArgumentException("Provided rating already exist in user: " + id);

    min = Math.min(rating, min);
    max = Math.max(rating, max);
    average = (average * (this.itemsRatings.size() - 1) + rating) / this.itemsRatings.size();
  }

  /**
   * Gets the minimum rating of the user
   *
   * @return Minimum rating
   */
  public double getMinRating() {
    return min;
  }

  /**
   * Gets the maximum rating of the user
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
