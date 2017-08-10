package com.github.basking2.sdsai.itrex.util;

public class Reflection {

    public static boolean isAssignable(Class<?> c1, Class<?> c2) {
        if (c1.isPrimitive()) {
            c1 = unbox(c1);
        }

        if (c2.isPrimitive()) {
            c2 = unbox(c2);
        }

        return c1.isAssignableFrom(c2);
    }

    public static Class<?> unbox(final Class<?> clazz) {

        if (int.class.equals(clazz)) {
            return Integer.class;
        } else if (long.class.equals(clazz)) {
            return Long.class;
        } else if (float.class.equals(clazz)) {
            return Float.class;
        } else if (double.class.equals(clazz)) {
            return Double.class;
        } else if (byte.class.equals(clazz)) {
            return Byte.class;
        } else if (char.class.equals(clazz)) {
            return Character.class;
        }
        else {
            return clazz;
        }
    }
}
