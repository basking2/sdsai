/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

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
import java.util.function.BiConsumer;
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

    /**
     * Given two sorted iterators, merge them.
     *
     * This is essentially the algorithm used by Merge Sort to merge two sorted list segments
     * into a final list, but when two keys are equal ({@link Comparable#compareTo(Object)} is 0)
     * then the consumer function is called.
     *
     * @param itr1 An iterator that provides values for which toKey1 will return ascending keys.
     * @param toKey1 A mapping function from values to keys.
     * @param itr2 An iterator that provides values for which toKey2 will return ascending keys.
     * @param toKey2 A mapping function from values to keys.
     * @param consumer The user's method of consuming matching values.
     * @param <K> The key type.
     * @param <T1> The type of elements of iterator 1.
     * @param <T2> The type of elements of iterator 2.
     * @return An array that reports the statistics of the merge operation.
     *         This array is 3 elements long.
     *         The first value is the number of left-values that have no match on the right (T1).
     *         The third value is the number of the right-values that have no match on the left (T2).
     *         The second value, the middle value, is the number of elemnts for which a match is found.
     */
    public static <K extends Comparable<K>, T1, T2> long[] mergeSorted(
            final Iterator<T1> itr1,
            final Function<T1, K> toKey1,
            final Iterator<T2> itr2,
            final Function<T2, K> toKey2,
            final BiConsumer<T1, T2> consumer
    ) {
        final long[] stats = new long[]{0L, 0L, 0L};
        if (itr1.hasNext() && itr2.hasNext()) {
            T1 v1 = itr1.next();
            T2 v2 = itr2.next();

            while (true) {
                // Do the comparison.
                int cmp = toKey1.apply(v1).compareTo(toKey2.apply(v2));

                if (cmp == 0) {
                    // The two keys are equal! Merge them.
                    consumer.accept(v1, v2);
                    stats[1] += 1L;
                    if (itr1.hasNext() && itr2.hasNext()) {
                        // We've merged the current nodes. Now advance both iterators.
                        // If we cannot advance them _both_ we exit.
                        v1 = itr1.next();
                        v2 = itr2.next();
                    } else {
                        // Exit if we cannot advance both iterators.
                        return stats;
                    }
                } else if (cmp < 0) {
                    stats[0]+=1L;
                    if (itr1.hasNext()) {
                        // If key1 is  smaller, advance it.
                        v1 = itr1.next();
                    } else {
                        // If we cannot make progress on merging itr1, exit.
                        return stats;
                    }
                } else if (cmp > 0) {
                    stats[2]+=1L;
                    if (itr2.hasNext()) {
                        // If key2 is  smaller, advance it.
                        v2 = itr2.next();
                    } else {
                        // If we cannot make progress on merging itr1, exit.
                        return stats;
                    }
                }
            }
        }

        return stats;
    }

    /**
     * Given two sorted iterators, merge them.
     *
     * This is essentially the algorithm used by Merge Sort to merge two sorted list segments
     * into a final list, but when two keys are equal ({@link Comparable#compareTo(Object)} is 0)
     * then the consumer function is called.
     *
     * @param itr1 An iterator that provides values for which toKey1 will return descending keys.
     * @param toKey1 A mapping function from values to keys.
     * @param itr2 An iterator that provides values for which toKey2 will return descending keys.
     * @param toKey2 A mapping function from values to keys.
     * @param consumer The user's method of consuming matching values.
     * @param <K> The key type.
     * @param <T1> The type of elements of iterator 1.
     * @param <T2> The type of elements of iterator 2.
     * @return An array that reports the statistics of the merge operation.
     *         This array is 3 elements long.
     *         The first value is the number of left-values that have no match on the right (T1).
     *         The third value is the number of the right-values that have no match on the left (T2).
     *         The second value, the middle value, is the number of elemnts for which a match is found.
     */
    public static <K extends Comparable<K>, T1, T2> long[] mergeSortedDescending(
            final Iterator<T1> itr1,
            final Function<T1, K> toKey1,
            final Iterator<T2> itr2,
            final Function<T2, K> toKey2,
            final BiConsumer<T1, T2> consumer
    ) {
        final long[] stats = new long[]{0L, 0L, 0L};

        if (itr1.hasNext() && itr2.hasNext()) {
            T1 v1 = itr1.next();
            T2 v2 = itr2.next();

            while (true) {
                // Do the comparison.
                int cmp = toKey1.apply(v1).compareTo(toKey2.apply(v2));

                if (cmp == 0) {
                    // The two keys are equal! Merge them.
                    consumer.accept(v1, v2);
                    stats[1]+=1L;
                    if (itr1.hasNext() && itr2.hasNext()) {
                        // We've merged the current nodes. Now advance both iterators.
                        // If we cannot advance them _both_ we exit.
                        v1 = itr1.next();
                        v2 = itr2.next();
                    } else {
                        // Exit if we cannot advance both iterators.
                        return stats;
                    }
                } else if (cmp > 0) {
                    stats[0]+=1L;
                    if (itr1.hasNext()) {
                        // If key1 is  smaller, advance it.
                        v1 = itr1.next();
                    } else {
                        // If we cannot make progress on merging itr1, exit.
                        return stats;
                    }
                } else if (cmp < 0) {
                    stats[2]+=1L;
                    if (itr2.hasNext()) {
                        // If key2 is  smaller, advance it.
                        v2 = itr2.next();
                    } else {
                        // If we cannot make progress on merging itr1, exit.
                        return stats;
                    }
                }
            }
        }

        return stats;
    }

    /**
     * Given two sorted iterators, merge them.
     *
     * This is essentially the algorithm used by Merge Sort to merge two sorted list segments
     * into a final list, but when two keys are equal ({@link Comparable#compareTo(Object)} is 0)
     * then the consumer function is called. If the keys are not equal, the lower one iks passed
     * to the function with the other value as null.
     *
     * @param itr1 An iterator that provides values for which toKey1 will return descending keys.
     * @param toKey1 A mapping function from values to keys.
     * @param itr2 An iterator that provides values for which toKey2 will return descending keys.
     * @param toKey2 A mapping function from values to keys.
     * @param consumer The user's method of consuming matching values.
     * @param <K> The key type.
     * @param <T1> The type of elements of iterator 1.
     * @param <T2> The type of elements of iterator 2.
     * @return An array that reports the statistics of the merge operation.
     *         This array is 3 elements long.
     *         The first value is the number of left-values that have no match on the right (T1).
     *         The third value is the number of the right-values that have no match on the left (T2).
     *         The second value, the middle value, is the number of elemnts for which a match is found.
     */
    public static <K extends Comparable<K>, T1, T2> long[] mergeOrHandleSorted(
            final Iterator<T1> itr1,
            final Function<T1, K> toKey1,
            final Iterator<T2> itr2,
            final Function<T2, K> toKey2,
            final BiConsumer<T1, T2> consumer
    ) {
        final long[] stats = new long[]{0L, 0L, 0L};

        if (itr1.hasNext() && itr2.hasNext()) {
            T1 v1 = itr1.next();
            T2 v2 = itr2.next();

            while (true) {
                // Do the comparison.
                int cmp = toKey1.apply(v1).compareTo(toKey2.apply(v2));

                if (cmp == 0) {
                    // The two keys are equal! Merge them.
                    consumer.accept(v1, v2);
                    stats[1]+=1L;
                    if (itr1.hasNext() && itr2.hasNext()) {
                        // We've merged the current nodes. Now advance both iterators.
                        // If we cannot advance them _both_ we exit.
                        v1 = itr1.next();
                        v2 = itr2.next();
                    } else {
                        while (itr1.hasNext()) {
                            v1 = itr1.next();
                            consumer.accept(v1, null);
                            stats[0] += 1L;
                        }
                        while (itr2.hasNext()) {
                            v2 = itr2.next();
                            consumer.accept(null, v2);
                            stats[2] += 1L;
                        }
                        // Exit if we cannot advance both iterators.
                        return stats;
                    }
                } else if (cmp < 0) {
                    consumer.accept(v1, null);
                    stats[0]+=1L;
                    if (itr1.hasNext()) {
                        // If key1 is  smaller, advance it.
                        v1 = itr1.next();
                    } else {
                        while (itr2.hasNext()) {
                            v2 = itr2.next();
                            consumer.accept(null, v2);
                            stats[2] += 1L;
                        }
                        // If we cannot make progress on merging itr1, exit.
                        return stats;
                    }
                } else if (cmp > 0) {
                    consumer.accept(null, v2);
                    stats[2]+=1L;
                    if (itr2.hasNext()) {
                        // If key2 is  smaller, advance it.
                        v2 = itr2.next();
                    } else {
                        while (itr1.hasNext()) {
                            v1 = itr1.next();
                            consumer.accept(v1, null);
                            stats[0] += 1L;
                        }
                        // If we cannot make progress on merging itr1, exit.
                        return stats;
                    }
                }
            }
        }

        return stats;
    }
}
