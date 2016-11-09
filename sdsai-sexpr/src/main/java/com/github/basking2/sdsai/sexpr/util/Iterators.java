package com.github.basking2.sdsai.sexpr.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

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

    /**
     * Wrap one or more values into an iterator.
     *
     * @param ts The t values to wrap into an interator.
     *
     * @param <T> The type returned by the returned iterator.
     * @return An interator that will walk through ts values.
     */
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

    /**
     * Construct and return a {@link NullSkippingIterator}.
     *
     * @param iterator The iterator.
     * @param <T> A type for iterator.
     * @return A {@link NullSkippingIterator}.
     */
    public static <T> Iterator<T> skipNulls(final Iterator<T> iterator) {
        return new NullSkippingIterator<T>(iterator);
    }

    /**
     * Construct and return a {@link MappingIterator}.
     *
     * @param iterator The iterator that provides the input T values.
     * @param f The mapping function.
     * @param <T> The input type.
     * @param <R> The output type.
     * @return a {@link MappingIterator}.
     */
    public static <T, R> MappingIterator<T, R> mappingIterator(final Iterator<T> iterator, final MappingIterator.Mapper<T, R> f) {
        return new MappingIterator<T, R>(iterator, f);
    }
}
