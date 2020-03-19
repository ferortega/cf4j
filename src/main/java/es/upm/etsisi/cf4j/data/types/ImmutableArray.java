package es.upm.etsisi.cf4j.data.types;

public class Immutable<T> {

    final T[] immutableArray;

    Immutable(T[] array){
        this.immutableArray = array;
    }

    public final T get(int index) {
        return immutableArray[index];
    }

    
}
