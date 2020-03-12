package es.upm.etsisi.cf4j.data.types;

import java.util.ArrayList;

public class SortedArrayList<T>  extends ArrayList<T> {

    public SortedArrayList(){
        super();
    }

    public SortedArrayList(int customCapacity){
        super(customCapacity);
    }

    @Override
    public void add (int index, T element){
        //Add at certain position is not allowed.
        //Index is ignored.
        addReturningIndex(element);
    }

    @Override
    public boolean add (T element){
        addReturningIndex(element);
        return true; //Not needed
    }

    public int addReturningIndex(T element){

        int low = 0, high = this.size() -1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            @SuppressWarnings("unchecked") //This won't happen.
            final Comparable<? super T> midElement = (Comparable<T>) this.get(mid);
            if (midElement.compareTo(element) > 0) {
                high = mid - 1;
            } else if (midElement.compareTo(element) < 0) {
                low = mid + 1;
            } else {
                //If element exist, override.
                this.set(mid, element);
                return mid;
            }
        }

        //Element not found, usual insertion
        super.add(low, element);
        return low;

    }



    /**
     * This method find the element with positional equivalence in the array (Following Comparable interface ordering).
     * @param element Element to find position correspondences.
     * @return Array element which corresponds with the given element position.
     */
    public int get (T element){

        int low = 0, high = this.size() -1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            @SuppressWarnings("unchecked") //This won't happen.
            final Comparable<? super T> midElement = (Comparable<T>) this.get(mid);
            if (midElement.compareTo(element) > 0) {
                high = mid - 1;
            } else if (midElement.compareTo(element) < 0) {
                low = mid + 1;
            } else {
                return mid;
            }
        }
        return -1; //If it doesnt exist.

    }
}
