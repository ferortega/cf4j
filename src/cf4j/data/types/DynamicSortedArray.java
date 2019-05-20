package cf4j.data.types;

public class DynamicSortedArray<E> extends DynamicArray<E> {

    public DynamicSortedArray (){
        super();
    }

    public DynamicSortedArray (int customCapacity){
        super(customCapacity);
    }
    /**
     * <p>Adds any element to the array in a ordered way</p>
     * @param element Element to insert inside the sorted array.
     * @return Position where the element was positioned.
     */
    public int add(E element){
        if (this.size()==0){
            this.add(0,element);
            return 0;
        }
        int min = 0, max = this.size() -1;
        while (min < max) {
            int center = ((max - min) / 2) + min;
            @SuppressWarnings("unchecked") //This won't happen.
            final E e = (E) data[center];
            @SuppressWarnings("unchecked") //This won't happen.
            final Comparable<E> c = (Comparable<E>) data[center];

            if (c.compareTo(element) > 0) { //TODO: Comprobar.
                max = center - 1;
            } else {
                min = center + 1;
            }
        }
        @SuppressWarnings("unchecked") //This won't happen.
        final E e = (E) data[min];
        @SuppressWarnings("unchecked") //This won't happen.
        final Comparable<E> c = (Comparable<E>) data[min];
        if(c.compareTo(element) > 0){ //TODO: Comprobar.
            this.add(min+1,element);
            return min+1;
        }else{
            this.add(min,element);
            return min;
        }
    }

    /**
     * This methods find the element with positional equivalence in the array (Followin Comparable interface ordering).
     * @param element Element to find position correspondences.
     * @return Array element which corresponds with the given element position.
     * @throws ClassCastException
     */
    public int get (E element) throws ClassCastException{
        int min = 0, max = this.size() -1;
        while (min <= max) {
            int center = ((max - min) / 2) + min;
            @SuppressWarnings("unchecked") //This won't happen.
            final E e = (E) data[center];
            @SuppressWarnings("unchecked") //This won't happen.
            final Comparable<E> c = (Comparable<E>) data[center];
            if (c.compareTo(element) == 0)
                return center;
            if (c.compareTo(element) > 0) { //TODO: Comprobar.
                max = center - 1;
            } else {
                min = center + 1;
            }
        }
        return -1; //If it doesnt exist.
    }
}
