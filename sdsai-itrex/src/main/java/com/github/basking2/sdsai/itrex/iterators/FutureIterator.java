/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.itrex.iterators;

import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

/**
 * A class that wraps an iterator such that calls to {@link #next()} result in a {@link Future} being returned.
 *
 * The wrapped iterator must be thread-safe.
 */
public class FutureIterator<T> implements Iterator<Future<T>> {
    private final Iterator<T> iterator;
    private final Executor executor;

    /**
     * Create a new FutureIterator.
     *
     * @param iterator A thread-safe iterator.
     * @param executor The executor {@link Future}s will be completed in.
     */
    public FutureIterator(final Iterator<T> iterator, final Executor executor) {
        this.iterator = iterator;
        this.executor = executor;
    }

    /**
     * Return true if there are more elements in the source iterator.
     *
     * This call is not reliable as a call to {@link Iterator#next()} may be happening
     * as this call returns.
     *
     * @return true if, at the moment {@link Iterator#hasNext()} is called on the wrapped
     * iterator, it returns true. False otherwise.
     */
    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Future<T> next() {
        final CompletableFuture<T> f = new CompletableFuture<>();
        executor.execute(() -> {
            try {
                f.complete(iterator.next());
            }
            catch (final Throwable t) {
                f.completeExceptionally(t);
            }
        });
        return f;
    }
}
