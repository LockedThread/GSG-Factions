package com.gameservergroup.gsgcore.triple;

public interface Triple<L, M, R> {

    static <L, M, R> Triple<L, M, R> of(L l, M m, R r) {
        return new ImmutableTriple<>(l, m, r);
    }

    L getLeft();

    void setLeft(L k);

    M getMiddle();

    void setMiddle(M v);

    R getRight();

    void setRight(R r);
}
