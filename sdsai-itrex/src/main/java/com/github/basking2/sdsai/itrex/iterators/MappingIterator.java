/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.itrex.iterators;

import com.github.basking2.sdsai.itrex.SExprRuntimeException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator that wraps another {@link Iterator} and maps its output values to values this returns.
 *
 * Any {@link Exception} thrown during the mapping results in a {@link NoSuchElementException} being thrown
 * from this class's {@link #next()} method.
 */
public class MappingIterator<T, R> implements Iterator<R> {

    private final Iterator<T> iterator;
    private final Mapper<T,R> f;

    public MappingIterator(final Iterator<T> iterator, final Mapper<T, R> f) {
        this.iterator = iterator;
        this.f = f;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public R next() {
        try {
            return f.map(iterator.next());
        }
        catch (final NoSuchElementException e) {
            throw e;
        }
        catch (final Throwable e) {
            throw new SExprRuntimeException(e.getMessage(), e);
        }
    }

    @FunctionalInterface
    public interface Mapper<T, R> {
        R map(T r) throws Exception;
    }
}
