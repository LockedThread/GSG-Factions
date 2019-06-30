package com.gameservergroup.gsgcore.pair;

public interface Pair<K, V> {

    static <K, V> Pair<K, V> of(K k, V v) {
        return new ImmutablePair<>(k, v);
    }

    K getKey();

    void setKey(K k);

    V getValue();

    void setValue(V v);
}
