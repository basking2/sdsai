/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.itrex.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Return the first N elements of another iterator.
 *
 * This is used to implement the {@link PagingIterator}.
 *
 * @param <T> The type.
 */
public class HeadIterator<T> implements Iterator<T> {
    private int left;
    private final Iterator<T> iterator;

    public HeadIterator(final int n, final Iterator<T> iterator) {
        this.left = n;
        this.iterator = iterator;
    }

    public HeadIterator(final Iterator<T> iterator) {
        this(1, iterator);
    }

    @Override
    public boolean hasNext() {
        return left > 0 && iterator.hasNext();
    }

    @Override
    public T next() {
        if (left < 1) {
            throw new NoSuchElementException("No elements left in HeadIterator.");
        }

        left--;

        return iterator.next();
    }
}
