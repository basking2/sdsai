package com.github.basking2.sdsai.itrex.util;

import java.util.Iterator;
import java.util.concurrent.Executor;

import static java.util.Arrays.asList;

/**
 * An iterator that prefetches and caches some number of elements from another iterator.
 *
 * Sometimes it is expensive for an iterator to return a value.
 * In these situations it is sometimes preferable to pre-fetch a few objects, and
 * asynchronously replenish them.
 *
 * Order is preserved, so if there is a particularly slow fetch, it may stall future fetches.
 * 
 * This is just a kind of {@link ParallelIteratorIterator} that uses a single iterator.
 */
public class PrefetchingIterator<T> extends ParallelIteratorIterator<T> {

    public PrefetchingIterator(final Executor executor, final int prefetch, final Iterator<T> iterator) {
        super(executor, prefetch, asList(iterator));
    }
}
