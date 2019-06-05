package com.gameservergroup.gsgcore.pair;

public class MutablePair<K, V> implements Pair<K, V> {

    private K k;
    private V v;

    public MutablePair(K k, V v) {
        this.k = k;
        this.v = v;
    }

    @Override
    public K getKey() {
        return k;
    }

    @Override
    public void setKey(K k) {
        this.k = k;
    }

    @Override
    public V getValue() {
        return v;
    }

    @Override
    public void setValue(V v) {
        this.v = v;
    }
}