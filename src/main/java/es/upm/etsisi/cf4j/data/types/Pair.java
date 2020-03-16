package es.upm.etsisi.cf4j.data.types;

import java.io.Serializable;

public class Pair <K extends Comparable<K>, V extends Serializable> implements Serializable, Comparable<Pair<K, V>> {

    public K key;
    public V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) {
            return false;
        }
        Pair<?, ?> p = (Pair<?, ?>) o;
        return key.equals(p.key) && value.equals(p.value);
    }

    @Override
    public int hashCode() {
        return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
    }

    @Override
    public int compareTo(Pair<K, V> o) {
        return key.compareTo(o.key);
    }
}
