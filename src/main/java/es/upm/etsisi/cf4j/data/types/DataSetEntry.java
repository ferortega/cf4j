package es.upm.etsisi.cf4j.data.types;

/**
 * Structural class which stores Triplets: user identifier (String), item identifier (string), and
 * rating value. This class is created to simplify the instantiation of a DataModel thorough a
 * DataSet. DataSetEntry is immutable, so the access to their attributes are public, since we don't
 * need the encapsulation concept.
 */
public class DataSetEntry {

  /** Identifier of the user who rated this item */
  public final String userId;

  /** Identifier of the item rated by the user */
  public final String itemId;

  /** Rating value */
  public final double rating;

  /**
   * Creates a new DataSet entry form an user identifier, an item identifier and a rating value.
   *
   * @param userId User identifier
   * @param itemId Item identifier.
   * @param rating Rating value
   */
  public DataSetEntry(String userId, String itemId, double rating) {
    this.userId = userId;
    this.itemId = itemId;
    this.rating = rating;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof DataSetEntry)) {
      return false;
    }
    DataSetEntry p = (DataSetEntry) o;
    return userId.equals(p.userId) && itemId.equals(p.itemId) && rating == p.rating;
  }

  @Override
  public int hashCode() {
    return (userId == null ? 0 : userId.hashCode()) ^ (itemId == null ? 0 : itemId.hashCode());
  }

  @Override
  public String toString() {
    return "<" + userId + "," + itemId + "," + rating + ">";
  }
}
