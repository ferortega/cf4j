package cf4j.data.types;

public class DynamicArray<E> {

    static final short INITIAL_CAPACITY = 10;

    protected Object [] data;
    protected int size;

    public DynamicArray(){
        this.data =  new Object[INITIAL_CAPACITY];
        this.size = 0;
    }

    public DynamicArray(int customCapacity){
        this.data =  new Object[customCapacity];
        this.size = 0;
    }

    /**
     * <p>Insert element in a specific position.</p>
     * <p>This will move each other elements right.</p>
     * @param index Position to insert the element.
     * @param element Element to add ordered
     * @throws IndexOutOfBoundsException
     */
    public void add(int index, E element) throws IndexOutOfBoundsException{
        if (index < 0 || index > this.size) //Allowing insertion at last position
            throw new IndexOutOfBoundsException("Entered index '" + index + "' is out of bounds.");

        if (this.size >= this.data.length)
            this.increaseCapacity(this.size);

        System.arraycopy(this.data,index,this.data,index+1,this.size - index);
        this.data[index] = element;
        this.size++;

        if (index >= this.size)
            this.size = index + 1;
    }

    /**
     * <p>Insert element in a specific position, overwriting its contents.</p>
     * <p>If the index exceeds current size, it will be added as new element in the array increasing the size</p>
     * @param index Position to insert/add the element.
     * @param element Element to add ordered
     * @throws IndexOutOfBoundsException
     */
    public void modify(int index, E element){
        if (index >= 0 && index < this.size){
            this.data[index] = element;
        }

        if (index >= this.size) {
            this.size = index + 1;

            if (this.size >= this.data.length)
                this.increaseCapacity(this.size);

            this.data[index] = element;
        }
    }
    /**
     * <p>This method returns the element in a specific position in the array [0-size()-1]</p>
     * @param index Index to the element inside the array.
     * @return The element, or null if it's not setted.
     * @throws IndexOutOfBoundsException
     */
    public E get (int index) throws IndexOutOfBoundsException {
        if (index<0 || index>=this.size)
            throw new IndexOutOfBoundsException("Entered index '" + index + "' is out of bounds.");

        @SuppressWarnings("unchecked") //This won't happen.
        final E e = (E) data[index]; //TODO: Devuelve null si no está setteado?
        return e;
    }

    public int size(){
        return this.size;
    }

    protected void increaseCapacity (int toAllocate){
        final Object[] newData = new Object [toAllocate * 2];
        System.arraycopy(this.data, 0, newData,0, this.data.length);
        this.data = newData; //Garbage collector, it's your turn.
    }

//    public boolean remove (Object o){
//        //TODO: Busqueda dicotómica y utilizar this.remove(index). (Creo que nos obliga a hacerlo por hash, debido a que no tiene tipo.
//        return false;
//    }
}
