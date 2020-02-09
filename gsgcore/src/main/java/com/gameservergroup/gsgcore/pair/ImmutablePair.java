package com.gameservergroup.gsgcore.pair;

public class ImmutablePair<K, V> implements Pair<K, V> {

    private final K key;
    private final V value;

    public ImmutablePair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public void setKey(K key) {
        throw new UnsupportedOperationException("This pair is immutable and therefore the fields can't be changed.");
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public void setValue(V value) {
        throw new UnsupportedOperationException("This pair is immutable and therefore the fields can't be changed.");
    }

    @Override
    public String toString() {
        return "ImmutablePair{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }
}
