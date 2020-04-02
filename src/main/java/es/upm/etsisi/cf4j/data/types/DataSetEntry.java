package es.upm.etsisi.cf4j.data.types;

/**
 * Structural class which stores Triplets: User identification (string), Item identification (string), and Rating made.
 * This class is created to ensure the communication between the different DataSets and the DataModel classes.
 * DataSetEntry is unmutable, so the access to their attributes are public, since we don't need the encapsulation concept.
 */
public class DataSetEntry{

    /**
     * Identification of the user who rated this item.
     */
    public final String userId;

    /**
     * Identification of the item rated by the user.
     */
    public final String itemId;

    /**
     * Rating made by the user.
     */
    public final double rating ;

    /**
     * Creates a new DataSet entry form an user identification, an item identification and a rating.
     * @param userId User identification code.
     * @param itemId Item identification code.
     * @param rating Rating made by the user to this item.
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