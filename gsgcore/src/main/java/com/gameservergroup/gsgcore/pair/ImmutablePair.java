package com.gameservergroup.gsgcore.pair;

public class ImmutablePair<K, V> implements Pair<K, V> {

    private final K k;
    private final V v;

    public ImmutablePair(K k, V v) {
        this.k = k;
        this.v = v;
    }

    @Override
    public K getKey() {
        return k;
    }

    @Override
    public void setKey(K k) {
        throw new UnsupportedOperationException("This pair is immutable and therefore the fields can't be changed.");
    }

    @Override
    public V getValue() {
        return v;
    }

    @Override
    public void setValue(V v) {
        throw new UnsupportedOperationException("This pair is immutable and therefore the fields can't be changed.");
    }
}
