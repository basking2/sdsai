package com.github.basking2.sdsai.itrex.util;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * A class that wraps an iterator such that calls to {@link #next()} result in a {@link Future} being returned.
 */
public class FutureIterator<T> implements Iterator<Future<T>> {
    private final Iterator<T> iterator;
    private final ExecutorService executorService;

    public FutureIterator(final Iterator<T> iterator, final ExecutorService executorService) {
        this.iterator = iterator;
        this.executorService = executorService;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Future<T> next() {
        return executorService.submit(() -> iterator.next());
    }
}
