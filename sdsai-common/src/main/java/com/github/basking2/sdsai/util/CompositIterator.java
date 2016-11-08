package com.github.basking2.sdsai.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Maps iteration over a list of iterators that return the same type.
 *
 * @param <E>
 */
public class CompositIterator<E extends Object> implements Iterator<E>
{

    private Iterator<E>[] iterators;
    private int whichIterator;

    @SafeVarargs
    public CompositIterator(Iterator<E>... i)
    {
        this.iterators = i;
        this.whichIterator = 0;
    }

    @Override
    public boolean hasNext()
    {
        while ( whichIterator < iterators.length )
        {
            if ( iterators[whichIterator].hasNext() ) {
                return true;
            }
            else {
                whichIterator++;
            }
        }

        return false;
    }

    @Override
    public E next()
    {
        if ( whichIterator >= iterators.length ) {
            throw new NoSuchElementException();
        }

        final E e = iterators[whichIterator].next();

        // When exhausting one iterator, go to the next.
        if ( ! iterators[whichIterator].hasNext() ) {
            whichIterator++;
        }

        return e;
    }

    @Override
    public void remove()
    {
        if ( whichIterator >= iterators.length ) {
            throw new IllegalStateException();
        }

        iterators[whichIterator].remove();
    }

    /**
     * Append one or more iterators to the internal list.
     * This is a slow operation in that the internal list is
     * copied into a new array and then the submitted iterators
     * are copied into the last few slots.
     */
    @SafeVarargs
    final public void appendIterators(Iterator<E>... iters)
    {
        int prevLen = iterators.length;

        iterators = Arrays.copyOf(iterators, prevLen+iters.length);

        for ( int i = 0 ; i < iters.length ; i++ ) {
            iterators[prevLen + i] = iters[i];
        }
    }
}
