package es.upm.etsisi.cf4j.data.types;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * SortedRatingList is a specific type of sorted ArrayList that uses the Rating class internally. In
 * this way, this class is ordered by the indexes included in the structure defined by the Rating
 * class, i.e. &lt;index, rating$gt;. This class is only used by the other structural classes like
 * User, TestUser, Item and TestItem. Developers do not need to manipulate this class.
 */
public class SortedRatingList extends ArrayList<Rating> implements Serializable {

  /**
   * Adds an ordered rating to the SortedRatingList.
   *
   * @param index Index
   * @param rating Rating value
   * @return True if the element didn't exist previously. False otherwise.
   */
  public boolean add(int index, double rating) {
    int low = 0, high = this.size() - 1;
    while (low <= high) {
      int mid = (low + high) >>> 1;
      final Rating midElement = this.get(mid);
      if (midElement.getIndex() > index) {
        high = mid - 1;
      } else if (midElement.getIndex() < index) {
        low = mid + 1;
      } else {
        // If element exist, override.
        this.get(mid).setRating(rating);
        return false;
      }
    }

    // Element not found, usual insertion
    this.add(low, new Rating(index, rating));
    return true; // Not needed
  }

  /**
   * Finds the position of an index
   *
   * @param index Index to be found
   * @return Position which corresponds with the given index
   */
  public int find(int index) {
    int low = 0, high = this.size() - 1;
    while (low <= high) {
      int mid = (low + high) >>> 1;
      final Rating midElement = this.get(mid);
      if (midElement.getIndex() > index) {
        high = mid - 1;
      } else if (midElement.getIndex() < index) {
        low = mid + 1;
      } else {
        return mid;
      }
    }
    return -1; // If it doesn't exist.
  }
}
