package es.upm.etsisi.cf4j.data.types;

import java.io.Serializable;

/**
 * The class Rating is an structure of a pair of elements, which are the index and the rating. This
 * index can be associated to a User or an Item depending on the needs. Developers do not need to
 * manipulate this class.
 */
public class Rating implements Serializable, Comparable<Rating> {

  private static final long serialVersionUID = 20200314L;

  /** Index of the user or the item */
  private int index;

  /**
   * Rating associated to that index, understanding this rating explicitly related with other user
   * or item (opposed to the index stored in this class). In other words, this rating is explicitly
   * related with an item and an user.
   */
  private double rating;

  /**
   * Creates a new instance
   *
   * @param index Index of the user or item
   * @param rating Rating value
   */
  public Rating(int index, double rating) {
    this.index = index;
    this.rating = rating;
  }

  /**
   * Gets the stored index.
   *
   * @return Index
   */
  public int getIndex() {
    return this.index;
  }

  /**
   * Gets the rating value.
   *
   * @return Rating value
   */
  public double getRating() {
    return this.rating;
  }

  /**
   * Modifies the previously defined rating.
   *
   * @param value Rating value
   */
  public void setRating(double value) {
    this.rating = value;
  }

  @Override
  public int compareTo(Rating other) {
    return Integer.compare(this.index, other.index);
  }

  @Override
  public String toString() {
    return "<" + this.getIndex() + ',' + this.getRating() + '>';
  }
}
