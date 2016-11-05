package com.github.basking2.sdsai.sexpr.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Iterators {

    /**
     * Map lists to iterators.
     *
     * @param o
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> Iterator<T> toIterator(final Object o) {
        if (o instanceof Iterator) {
            return ((Iterator<T>)o);
        }

        if (o instanceof Iterable) {
            return ((Iterable<T>)o).iterator();
        }

        if (o instanceof Object[]) {
            return Arrays.asList((T[])o).iterator();
        }

        return null;
    }

    @SafeVarargs
    public static <T> Iterator<T> wrap(final T ... ts) {
        return new Iterator<T>() {

            int idx = 0;

            @Override
            public boolean hasNext() {
                return idx < ts.length;
            }

            @Override
            public T next() {
                try {
                    return ts[idx++];
                } catch (final ArrayIndexOutOfBoundsException e) {
                    throw new NoSuchElementException(e.getMessage());
                }
            }
        };
    }
}
