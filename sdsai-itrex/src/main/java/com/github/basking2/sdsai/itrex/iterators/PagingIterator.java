package com.github.basking2.sdsai.itrex.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This iterator wraps its input iterator in {@link HeadIterator}s until no elements remain in the source iterator.
 *
 * This structures the computation into pages of data. Users of this class must take care,
 * all {@link HeadIterator} instances share the source iterator and calling them in parallel will
 * yield unexpected results.
 *
 * A returned iterator should be exhausted before another is called.
 *
 * @param <T> The type of elements to page.
 */
public class PagingIterator<T> implements Iterator<Iterator<T>> {

    /**
     * The last iterator returned by this. This must be exhausted before another will be created.
     */
    private Iterator<T> headIterator;
    private final Iterator<T> iterator;
    private final int pageSize;

    public PagingIterator(final int pageSize, final Iterator<T> iterator) {
        this.pageSize = pageSize;
        this.iterator = iterator;
        this.headIterator = (Iterator<T>)Iterators.EMPTY_ITERATOR;
    }

    /**
     * Returns true if the source iterator has more elements to be paged over.
     *
     * If the user is consuming an iterator returned by a call to {@link #next()}, this may return inconsistent results.
     *
     * @return true if the source iterator has more elements to be paged over.
     */
    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    /**
     * Return a {@link PagingIterator} for the source iterator. The previously returned iterator <B>must</B> be empty.
     *
     * @throws IllegalStateException when the previously returned iterator is not fully consumed.
     * @return A {@link HeadIterator} that pages over elements of the input iterator.
     */
    @Override
    public Iterator<T> next() {
        if (headIterator.hasNext()) {
            throw new IllegalStateException("Previously returned iterator must be exhausted before another call to next().");
        }

        if (iterator.hasNext()) {
            headIterator = new HeadIterator<>(pageSize, iterator);
            return headIterator;
        }

        throw new NoSuchElementException();
    }
}
