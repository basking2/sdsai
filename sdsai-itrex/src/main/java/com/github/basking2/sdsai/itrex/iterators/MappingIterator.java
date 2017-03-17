package com.github.basking2.sdsai.itrex.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator that wraps another {@link Iterator} and maps its output values to values this returns.
 *
 * Any {@link Exception} thrown during the mapping results in a {@link NoSuchElementException} being thrown
 * from this class's {@link #next()} method.
 */
public class MappingIterator<T, R> implements Iterator<R> {

    private Iterator<? extends T> iterator;
    private Mapper<T,R> f;

    public MappingIterator(final Iterator<? extends T> iterator, final Mapper<T, R> f) {
        this.iterator = iterator;
        this.f = f;
    }

    public void setMapping(Mapper<T, R> f) {
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
        catch (final Exception e) {
            throw new NoSuchElementException(e.getMessage());
        }
    }

    @FunctionalInterface
    public interface Mapper<T, R> {
        R map(T r) throws Exception;
    }
}
