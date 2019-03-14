package com.gameservergroup.gsgcore.utils;

public interface CallBack {

    default void call() {
    }

    default void call(Object... objects) {
    }
}
