package org.chorusbdd.chorus.util.function;

/**
 * Created by nick on 12/01/15.
 */
public class Tuple2<K,V> {

    private K k;
    private V v;

    public Tuple2(K k, V v) {
        this.k = k;
        this.v = v;
    }

    public V getOne() {
        return v;
    }

    public K getTwo() {
        return k;
    }

    public static <K,V> Tuple2<K,V> tuple2(K k, V v) {
        return new Tuple2<K,V>(k, v);
    }
}
