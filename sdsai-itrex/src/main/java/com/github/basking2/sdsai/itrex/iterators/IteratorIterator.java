/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.itrex.iterators;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class IteratorIterator<T> implements Iterator<T> {

    final Iterator<Iterator<T>> inputs;
    Iterator<T> currentIterator;

    public IteratorIterator(final Iterator<Iterator<T>> inputs) {
        this.inputs = inputs;
        this.currentIterator = new Iterator<T>() {
            @Override public boolean hasNext() { return false; }
            @Override public T next() { throw new NoSuchElementException(); }
        };
    }

    public IteratorIterator(final List<Iterator<T>> inputs) {
        this(inputs.iterator());
    }

    /**
     * Ensure that currentIterator points to the next element if one exists.
     */
    private void updateCurrentIterator() {
        while (! currentIterator.hasNext() && inputs.hasNext()) {
            currentIterator = inputs.next();
        }
    }

    @Override public boolean hasNext() {
        updateCurrentIterator();

        return currentIterator.hasNext();
    }

    @Override public T next() {
        updateCurrentIterator();

        return currentIterator.next();
    }
}
