package cf4j.data.types;

import java.util.ArrayList;

public class DynamicArray<E>  extends ArrayList<E> {

    public DynamicArray(){
        super();
    }

    public DynamicArray(int customCapacity){
        super(customCapacity);
    }

    public int addOrdered(E element){
        if (this.size()==0){
            this.add(0,element);
            return 0;
        }
        int min = 0, max = this.size() -1;
        while (min < max) {
            int center = ((max - min) / 2) + min;
            final E e = this.get(center);
            @SuppressWarnings("unchecked") //This won't happen.
            final Comparable<E> c = (Comparable<E>) this.get(center);

            if (c.compareTo(element) == 0){
                this.set(center,element);
                return center;
            } else if (c.compareTo(element) > 0) {
                max = center - 1;
            } else {
                min = center + 1;
            }
        }

        final E e = this.get(min);
        @SuppressWarnings("unchecked") //This won't happen.
        final Comparable<E> c = (Comparable<E>) this.get(min);
        if(c.compareTo(element) < 0){
            this.add(min+1,element);
            return min+1;
        }else{
            this.add(min,element);
            return min;
        }
    }



    /**
     * This methods find the element with positional equivalence in the array (Following Comparable interface ordering).
     * @param element Element to find position correspondences.
     * @return Array element which corresponds with the given element position.
     */
    public int get (E element){
        int min = 0, max = this.size() -1;
        while (min <= max) {
            int center = ((max - min) / 2) + min;
            final E e = this.get(center);
            @SuppressWarnings("unchecked") //This won't happen.
            final Comparable<E> c = (Comparable<E>) this.get(center);
            if (c.compareTo(element) == 0)
                return center;
            if (c.compareTo(element) > 0) {
                max = center - 1;
            } else {
                min = center + 1;
            }
        }
        return -1; //If it doesnt exist.
    }
}
