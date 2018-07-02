package com.github.basking2.sdsai.itrex.iterators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Collect and present data from a set of {@link UncertainIterator}s.
 *
 * @param <T> The types being joined.
 */
public class JoinUncertainIterators<T> implements Iterator<T> {

    private final List<UncertainIterator<T>> iterators;
    private final UncertainIterator<UncertainIterator<T>> uncertainIteratorUncertainIterator;

    public JoinUncertainIterators(final UncertainIterator<UncertainIterator<T>> iterators) {
        this.iterators = new ArrayList<>();
        this.uncertainIteratorUncertainIterator = iterators;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public T next() {
        return null;
    }
}
