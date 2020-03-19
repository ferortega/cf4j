package es.upm.etsisi.cf4j.data.types;

public class ImmutableArray<T> {

    final T[] immutableArray;

    public ImmutableArray(T[] array){
        this.immutableArray = array;
    }

    public T get(int index) {
        return immutableArray[index];
    }

    public int getNumElements(){
        return immutableArray.length;
    }
}
