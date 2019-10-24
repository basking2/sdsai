package com.github.basking2.sdsai.itrex.iterators;

import com.github.basking2.sdsai.itrex.iterators.splitjoin.JoinUncertainIteratorsIterator;
import com.github.basking2.sdsai.itrex.iterators.splitjoin.SplitMapUncertainIterator;
import com.github.basking2.sdsai.itrex.util.TwoTuple;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Predicate;

public class Iterators {

    public static Iterator<?> EMPTY_ITERATOR = new Iterator<Object>() {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Object next() {
            throw new NoSuchElementException("Next called on the empty iterator.");
        }
    };

    /**
     * Map iterables and arrays to iterators or returns null.
     * <p>
     * If an iterator is passed it, it is returned.
     * <p>
     * This will not wrap a non-iterable object (or array) into a single element iterator. See {@link #wrap(Object[])} for that.
     *
     * @param o   An object we would like to try and convert to an {@link Iterator}.
     * @param <T> The type returned by the {@link Iterator}.
     * @return An iterator that walks over the elements in o, or null if we cannot convert o to an {@link Iterator}.
     */
    @SuppressWarnings("unchecked")
    public static <T> Iterator<T> toIterator(final Object o) {
        if (o instanceof Iterator) {
            return ((Iterator<T>) o);
        }

        if (o instanceof Iterable) {
            return ((Iterable<T>) o).iterator();
        }

        if (o instanceof Object[]) {
            return Arrays.asList((T[]) o).iterator();
        }

        return null;
    }

    /**
     * Return true if the object is an iterator or can be made into an iterator via {@link #toIterator(Object)}.
     * @param o The object to check.
     * @return true if the object is an iterator or can be made into an iterator via {@link #toIterator(Object)}.
     */
    public static boolean isIter(final Object o) {
        return
                (o instanceof Iterator) ||
                (o instanceof Iterable) ||
                (o instanceof Object[]);
    }

    /**
     * Wrap one or more values into an iterator.
     *
     * @param ts  The t values to wrap into an interator.
     * @param <T> The type returned by the returned iterator.
     * @return An interator that will walk through ts values.
     */
    @SafeVarargs
    public static <T> Iterator<T> wrap(final T... ts) {
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
     * @param <T>      A type for iterator.
     * @return A {@link NullSkippingIterator}.
     */
    public static <T> Iterator<T> skipNulls(final Iterator<T> iterator) {
        return new NullSkippingIterator<T>(iterator);
    }

    /**
     * Construct and return a {@link MappingIterator}.
     *
     * @param iterator The iterator that provides the input T values.
     * @param f        The mapping function.
     * @param <T>      The input type.
     * @param <R>      The output type.
     * @return a {@link MappingIterator}.
     */
    public static <T, R> MappingIterator<T, R> mapIterator(final Iterator<T> iterator, final MappingIterator.Mapper<T, R> f) {
        return new MappingIterator<T, R>(iterator, f);
    }

    /**
     * Build an {@link IteratorIterator} that will flatten the given iterator of iterators of T.
     *
     * @param iterator An iterator of iterators of type T.
     * @param <T>      The type to iterate.
     * @return An iterator that has flattened the iterator of iterators.
     */
    public static <T> IteratorIterator<T> flatten(final Iterator<Iterator<T>> iterator) {
        return new IteratorIterator<T>(iterator);
    }

    /**
     * Collect the elements of an iterator into a {@link List}.
     *
     * @param itr The iterator to materialize.
     * @param <T> The type of list elements.
     * @return a list for the iterator.
     */
    public static <T> List<T> toList(final Iterator<T> itr) {
        final ArrayList<T> list = new ArrayList<T>();
        while (itr.hasNext()) {
            list.add(itr.next());
        }
        return list;
    }

    public static <T> FilterIterator<T> filterIterator(final Iterator<T> iterator, final Predicate<T> p) {
        return new FilterIterator<>(iterator, p);
    }


    /**
     * Build an iterator that will concurrently process elements split from the source iterator.
     *
     * @param executor The executor service.
     * @param inputs Inputs to be split into many sub-iterations based on the split function.
     * @param splitFunction Map input types to keys that will group like items together for mapping in separate threads.
     * @param mapper The mapper that maps sub-iterations in individual tasks in the executor.
     * @param <R> The result type.
     * @param <T> The input type.
     * @param <K> The key type to group iterations by.
     *
     * @return An iterator that will manage all the complexities of splitting, mapping, and joining.
     */
    public static <R, T, K> Iterator<R> splitMapJoinIterator(
            final Executor executor,
            final Iterator<T> inputs,
            final Function<T, K> splitFunction,
            final MappingIterator.Mapper<T, R> mapper
    ) {
        return JoinUncertainIteratorsIterator.join(
                executor,
                new SplitMapUncertainIterator<>(inputs, splitFunction, mapper)
        );
    }

    @SuppressWarnings("unchecked")
    public static <T> Iterator<T> emptyIterator() {
        return (Iterator<T>) EMPTY_ITERATOR;
    }

    public static <T1, T2> Iterator<TwoTuple<T1, T2>> zip(
            final Iterator<T1> i1,
            final Iterator<T2> i2
    ) {
        return new ZipIterator<>(i1, i2);
    }

    public static <T1, T2> Iterator<TwoTuple<T1, T2>> zip(
            final Iterator<T1> i1, final boolean pad1, final T1 pad1Value,
            final Iterator<T2> i2, final boolean pad2, final T2 pad2Value
    ) {
        return new ZipIterator<>(i1, pad1, pad1Value, i2, pad2, pad2Value);
    }
}
