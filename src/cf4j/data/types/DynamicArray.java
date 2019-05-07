package cf4j.data.types;

public class DynamicArray<E> {

    static final short INCREMENT = 10;

    protected Object [] data;
    protected int size;

    public DynamicArray(){
        this.data =  new Object[INCREMENT];
        this.size = 0;
    }

    /**
     * <p>Insert element in a specific position.</p>
     * <p>Don't use this method if you are using sorted arrays</p>
     * @param index No sense index.
     * @param element Element to add ordered
     * @throws IndexOutOfBoundsException
     */
    public void add(int index, E element) throws IndexOutOfBoundsException{
        if (index >= this.data.length)
            this.increaseCapacity(index + INCREMENT);
        if (this.size >= this.data.length)
            this.increaseCapacity(this.size + INCREMENT);

        if (index<0 || index>this.size) //Allowing insertion at last position
            throw new IndexOutOfBoundsException("Entered index '" + index + "' is out of bounds.");

        System.arraycopy(this.data,index,this.data,index+1,this.data.length-index); //TODO: copia bien? se pisa?
        this.data[index] = element;
        this.size++;
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
        final Object[] newData = new Object [toAllocate];
        System.arraycopy(this.data, 0, newData,0, this.data.length);
        this.data = newData; //Garbage collector, it's your turn.
    }

//    public boolean remove (Object o){
//        //TODO: Busqueda dicotómica y utilizar this.remove(index). (Creo que nos obliga a hacerlo por hash, debido a que no tiene tipo.
//        return false;
//    }
}
