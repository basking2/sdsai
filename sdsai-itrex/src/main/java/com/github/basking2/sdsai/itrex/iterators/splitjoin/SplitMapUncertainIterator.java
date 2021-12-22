/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.itrex.iterators.splitjoin;

import com.github.basking2.sdsai.itrex.iterators.MappingIterator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * Split an iterator's elements across an UncertainIterator.
 *
 * This produces an object that may be processed and given to {@link JoinUncertainIteratorsIterator}.
 *
 * This object's operation is to take input elements from an iterator and pass them to
 * "child" iterators. Because we never know if a child iterator will get another element from the source stream,
 * based on the splitFunction, the child iterators must all be UncertainIterators.
 *
 * On calls to hasNext() an element is pulled from the input stream. It is checked if it will produce a new
 * output iterator. If it will, then we create and stage that output iterator to be returned on
 * the next call to next(). If the next element pulled maps to an existing iterator
 * we return MAYBE from hasNext() and no element is available from next().
 *
 * Recall that MAYBE should be considered FALSE until data is drawn from another source.
 */
public class SplitMapUncertainIterator<T, K, R> implements UncertainIterator<UncertainIterator<R>> {

    private final Iterator<T> inputs;
    private final Function<T, K> splitFunction;
    private final MappingIterator.Mapper<T, R> mapper;
    private final Map<K, CloseableUncertainIterator<T>> iterators;

    /**
     * hasNext computes this value. This field is a holder until next() is called.
     */
    private CloseableUncertainIterator<T> nextIterator;

    public SplitMapUncertainIterator(
            final Iterator<T> inputs,
            final Function<T, K> splitFunction,
            final MappingIterator.Mapper<T, R> mapper
    ) {
        this.iterators = new HashMap<>();
        this.splitFunction = splitFunction;
        this.inputs = inputs;
        this.mapper = mapper;
        this.nextIterator = null;
    }

    /**
     * Calling this populates iterators.
     *
     * If this returns MAYBE then a new element was enqueued into another iterator.
     *
     * If this returns TRUE then a new element caused a new iterator to be creatd.
     *
     * If this returns FALSE then no inputs remain. Whatever is in the output iterators is all the work that remains.
     *
     * @return If there is another iterator to be fetched.
     */
    @Override
    public HAS_NEXT hasNext() {

        // If the user calls hasNext() many times, make sure we don't keep mutating state.
        if (nextIterator != null) {
            return HAS_NEXT.TRUE;
        }

        // There is some data that may be mapped to a new iterator or an existing one.
        if (inputs.hasNext()) {
            final T t = inputs.next();
            final K k = splitFunction.apply(t);

            if (iterators.containsKey(k)) {
                final CloseableUncertainIterator<T> itr = iterators.get(k);
                itr.offer(t);
                return HAS_NEXT.MAYBE;
            }
            else {
                final CloseableUncertainIterator<T> itr = new CloseableUncertainIterator<>();
                itr.offer(t);
                iterators.put(k, itr);
                nextIterator = itr;
                return HAS_NEXT.TRUE;
            }
        }

        // Inputs have no more elements. Close and clean up everything.
        for (final CloseableUncertainIterator<T> itr : iterators.values()) {
            try {
                itr.close();
            } catch (final Exception e) {
                // This "should" not happen.
                e.printStackTrace();
            }
        }

        iterators.clear();

        return HAS_NEXT.FALSE;
    }

    @Override
    public UncertainIterator<R> next() {
        if (hasNext() != HAS_NEXT.TRUE) {
            throw new NoSuchElementException();
        }

        final CloseableUncertainIterator<T> iterator = nextIterator;
        nextIterator = null;
        return new MappingUncertainIterator<>(iterator, mapper);
    }
}
