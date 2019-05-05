package cf4j.utils;

import java.util.ArrayList;
import java.util.Collection;

public class OrderedIndexedArray<E> extends ArrayList<E> {

    /**
     * Ordered insertion in the array.
     * @param element Element to add ordered
     * @return If everithing is  inserted OK
     */
    @Override
    public boolean add(E element){
        //TODO: Dicotomic insertion using compareTo.
        return false;
    }
    /**
     * Don't use this method, it is the samen than add(element).
     * @param index No sense index.
     * @param element Element to add ordered
     */
    @Override
    public void add(int index, E element){
        add(element);
    }

    /**
     * This class ignores the index, it puts ordered a collection of elemets.
     * @param index No sense index
     * @param c Collection of elements
     * @return If everithing is inserted OK
     */
    @Override
    public boolean addAll(int index, Collection<? extends E> c)
    {
        boolean output = true;
        for(E element : c){output &= add(element);}
        return output;
    }
    @Override
    public E get (int index){
        //TODO: Búsqueda dicotómica del elemento exacto.
        return null;
    }
    @Override
    public int indexOf (Object o) {
        //TODO:Busqueda dicotómica del elemento exacto, pero devolviendo su posición.
        return 0;
    }
    @Override
    public int lastIndexOf(Object o){
        //TODO:Busqueda dicotómica del elemento exacto, pero devolviendo su posición.
        return 0;
    }
    @Override
    public boolean remove (Object o){
        //TODO: Busqueda dicotómica y utilizar this.remove(index). (Creo que nos obliga a hacerlo por hash, debido a que no tiene tipo.
        return false;
    }
    @Override
    public boolean removeAll(Collection<?> c){
        boolean output = true;
        for(Object element : c){output &= remove(element);}
        return output;
    }

    @Override
    public boolean retainAll(Collection<?>c){
        //TODO: Realmente no se que hace esto.
        return false;
    }
}
