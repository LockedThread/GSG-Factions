package com.gameservergroup.gsgcore.pair;

public interface Pair<K, V> {

    K getKey();

    void setKey(K k);

    V getValue();

    void setValue(V v);
}
