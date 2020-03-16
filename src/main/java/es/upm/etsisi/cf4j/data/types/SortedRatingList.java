package es.upm.etsisi.cf4j.data.types;

import java.util.ArrayList;
import org.apache.commons.lang3.tuple.Pair;

public class SortedRatingList extends ArrayList<Rating> {

    /**
     * This method adds an ordered rating to the SortedRatingList
     * @param index Index of the sorted item or user.
     * @param rating Rating done.
     * @return true if the element didn't exist previously. false otherwise.
     */
    public boolean add (int index, double rating){
        int low = 0, high = this.size() -1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            final Pair<Integer,Double> midElement = this.get(mid);
            if (midElement.getLeft() > index) {
                high = mid - 1;
            } else if (midElement.getLeft() < index) {
                low = mid + 1;
            } else {
                //If element exist, override.
                this.get(mid).setValue(rating);
                return false;
            }
        }

        //Element not found, usual insertion
        this.add(low, new Rating (index,rating));
        return true; //Not needed
    }

    /**
     * This method find the element with positional equivalence in the array.
     * @param index IndexToFind position correspondences.
     * @return Array element which corresponds with the given element position.
     */
    public int find (int index){
        int low = 0, high = this.size() -1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            final Pair<Integer,Double> midElement = this.get(mid);
            if (midElement.getLeft() > index) {
                high = mid - 1;
            } else if (midElement.getLeft() < index) {
                low = mid + 1;
            } else {
                return mid;
            }
        }
        return -1; //If it doesn't exist.
    }

}
