package com.github.basking2.sdsai.sexpr.util;

import java.util.Arrays;
import java.util.Iterator;

public class Iterators {
    public static <T> Iterator<T> toIterator(final Object o) {
        if (o instanceof Iterator) {
            return ((Iterator)o);
        }

        if (o instanceof Iterable) {
            return ((Iterable)o).iterator();
        }

        if (o instanceof Object[]) {
            return Arrays.asList((T[])o).iterator();
        }

        return null;
    }
}
