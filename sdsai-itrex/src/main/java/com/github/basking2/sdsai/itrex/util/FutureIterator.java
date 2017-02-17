package com.github.basking2.sdsai.itrex.util;

import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

/**
 * A class that wraps an iterator such that calls to {@link #next()} result in a {@link Future} being returned.
 */
public class FutureIterator<T> implements Iterator<Future<T>> {
    private final Iterator<T> iterator;
    private final Executor executor;

    public FutureIterator(final Iterator<T> iterator, final Executor executor) {
        this.iterator = iterator;
        this.executor = executor;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Future<T> next() {
        final CompletableFuture<T> f = new CompletableFuture<T>();
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
