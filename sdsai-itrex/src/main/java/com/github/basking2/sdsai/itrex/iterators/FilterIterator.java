package com.github.basking2.sdsai.itrex.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * Similar to the {@link NullSkippingIterator}, this will skip values if they fail a provided check.
 *
 * Skipping is done by prefetching 1 element to ensure we can accurately compute {@link #hasNext()}.
 *
 * @param <T> The element type to filter.
 */
public class FilterIterator<T> implements Iterator<T> {

    final private Predicate<T> predicate;
    final private Iterator<T> iterator;

    private T element;

    /**
     * Is {@link #element} defined by a successful call to {@link #predicate}?
     */
    private boolean haveElement;

    /**
     * @param iterator The iterator of elements.
     * @param predicate A predicate that checks elements returned by the iterator for inclusion in this iterator.
     *
     * @see NullSkippingIterator
     */
    public FilterIterator(final Iterator<T> iterator, final Predicate<T> predicate) {
        this.predicate = predicate;
        this.iterator = iterator;
        this.haveElement = true;
        updateElement();
    }

    /**
     * Update {@link #element} with a new value from {@link #iterator} that satisfies {@link #predicate}.
     *
     * If no such element is found, then iteration is completed and {@link #haveElement} is set to false.
     *
     * @return the next element that satisfies the {@link #predicate} or return null.
     */
    private void updateElement() {
        while (iterator.hasNext()) {
            final T t = iterator.next();

            if (predicate.test(t)) {
                element = t;
                return;
            }
        }

        element = null;
        haveElement = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        // We have next if we have an element.
        return haveElement;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T next() {
        // Iterator protocol.
        if (!haveElement) {
            throw new NoSuchElementException();
        }

        // Store a reference.
        final T toReturn = element;

        // Update that reference.
        updateElement();

        // Return.
        return toReturn;
    }
}
