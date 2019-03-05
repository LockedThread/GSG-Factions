package com.gameservergroup.gsgcore.utils;

import java.util.Arrays;

public enum PrimitiveClass {

    BOOLEAN(boolean.class, Boolean.FALSE.getClass(), Boolean.TRUE.getClass()),
    BYTE(byte.class, Byte.class),
    DOUBLE(double.class, Double.class),
    INT(int.class, Integer.class),
    LONG(long.class, Long.class),
    SHORT(short.class, Short.class),
    STRING(String.class);

    private Class[] classes;

    PrimitiveClass(Class... classes) {
        this.classes = classes;
    }

    public static PrimitiveClass get(Class aClass) {
        try {
            return valueOf(aClass.getSimpleName().toUpperCase());
        } catch (IllegalArgumentException ignored) {
        }
        return Arrays.stream(values())
                .filter(value -> Arrays.stream(value.classes).anyMatch(clazz -> clazz.getSimpleName().equalsIgnoreCase(aClass.getSimpleName())))
                .findFirst()
                .orElse(null);
    }
}
