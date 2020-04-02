package es.upm.etsisi.cf4j.data.types;

import java.io.Serializable;

/**
 * The class Rating is a structure of a pair of elements, which are the index and the rating. This index can be
 * associated to a User or an Item depending on the needs.
 */
public class Rating implements Serializable, Comparable<Rating> {

    private static final long serialVersionUID = 20200314L;

    /**
     * Index of the user or the item respectively.
     */
    private int index;

    /**
     * Rating associated to that index, understanding this rating explicitly related with other user or item (opposed
     * to the index stored in this class). In other, words, this rating is explicitly related wit an item and an user.
     */
    private double rating;

    /**
     * Creates a new instance of a Rating made by an user or made to an item.
     * @param index Index of the user or item.
     * @param rating Rating made by the user or made to the item.
     */
    public Rating (int index, double rating)
    {
        this.index = index;
        this.rating = rating;
    }

    /**
     * Get the stored index.
     * @return Index of the user or item.
     */
    public Integer getIndex() {
        return this.index;
    }

    /**
     * Get the rating made.
     * @return Rating made by the user or made to the item.
     */
    public Double getRating() {
        return this.rating;
    }

    /**
     * Modify the previously defined rating (This should not happen inside the datamodel).
     * @param value New Rating to be stored in this structural class.
     */
    public void setRating(Double value) {
        this.rating = value;
    }

    /**
     * Implementation of the interface Comparable. Ratings are only compared by the index, rating doesn't affect to the
     * comparision.
     * @param other Other rating to be compared with this rating.
     * @return 1, 0 or -1, depending on whether one is greater than the other or whether they are the same (0).
     */
    public int compareTo(Rating other) {
        return Integer.compare(this.index, other.index);
    }

    @Override
    public String toString() {
        return "(" + this.getIndex() + ',' + this.getRating() + ')';
    }
}
