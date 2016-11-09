package com.github.basking2.sdsai.sexpr.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator that pre-fetches the next value from the iterator it encloses so as to skip nulls.
 */
public class NullSkippingIterator<T> implements Iterator<T> {

    final private Iterator<T> iterator;
    private T nextT;

    public NullSkippingIterator(final Iterator<T> iterator)
    {
        this.iterator = iterator;
        prefetch();
    }

    private void prefetch() {
        nextT = null;
        while (iterator.hasNext() && nextT == null) {
            nextT = iterator.next();
        }
    }

    @Override
    public boolean hasNext() {
        return nextT != null;
    }

    @Override
    public T next() {
        if (nextT == null) {
            throw new NoSuchElementException();
        }

        final T t = nextT;

        prefetch();

        return t;
    }
}
