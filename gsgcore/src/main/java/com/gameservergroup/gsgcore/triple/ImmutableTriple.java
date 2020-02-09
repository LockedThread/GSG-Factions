package com.gameservergroup.gsgcore.triple;

public class ImmutableTriple<L, M, R> implements Triple<L, M, R> {

    private final L left;
    private final M middle;
    private final R right;

    public ImmutableTriple(L left, M middle, R right) {
        this.left = left;
        this.middle = middle;
        this.right = right;
    }

    @Override
    public L getLeft() {
        return left;
    }

    @Override
    public void setLeft(L k) {
        throw new UnsupportedOperationException("This pair is immutable and therefore the fields can't be changed.");
    }

    @Override
    public M getMiddle() {
        return middle;
    }

    @Override
    public void setMiddle(M v) {
        throw new UnsupportedOperationException("This pair is immutable and therefore the fields can't be changed.");

    }

    @Override
    public R getRight() {
        return right;
    }

    @Override
    public void setRight(R r) {
        throw new UnsupportedOperationException("This pair is immutable and therefore the fields can't be changed.");
    }

    @Override
    public String toString() {
        return "ImmutableTriple{" +
                "left=" + left +
                ", middle=" + middle +
                ", right=" + right +
                '}';
    }
}
