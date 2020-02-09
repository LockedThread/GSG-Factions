package com.gameservergroup.gsgcore.triple;

public class MutableTriple<L, M, R> implements Triple<L, M, R> {

    private L left;
    private M middle;
    private R right;

    public MutableTriple(L left, M middle, R right) {
        this.left = left;
        this.middle = middle;
        this.right = right;
    }

    @Override
    public L getLeft() {
        return left;
    }

    @Override
    public M getMiddle() {
        return middle;
    }

    @Override
    public R getRight() {
        return right;
    }

    @Override
    public void setLeft(L left) {
        this.left = left;
    }

    @Override
    public void setMiddle(M middle) {
        this.middle = middle;
    }

    @Override
    public void setRight(R right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return "MutableTriple{" +
                "left=" + left +
                ", middle=" + middle +
                ", right=" + right +
                '}';
    }
}
