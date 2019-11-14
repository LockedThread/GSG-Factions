package com.gameservergroup.gsgbots.utils;

import java.lang.reflect.Field;

public class Utils {

    public static Field getPrivateField(Class clazz, String field) {
        try {
            Field declaredField = clazz.getDeclaredField(field);
            declaredField.setAccessible(true);
            return declaredField;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }
}
