package com.gameservergroup.gsgcore.utils;

public interface CallBack<T> {

    default void call() {

    }

    default void call(T t) {

    }
}
