package es.upm.etsisi.cf4j.data.types;

public class DataSetEntry{

    public final String userId;
    public final String itemId;
    public final double rating ;

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