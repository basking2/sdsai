package com.github.basking2.sdsai.itrex.iterators.splitjoin;

/**
 * This is a type of iterator that has a "maybe" state as a result to calling hasNext().
 *
 * The semantic meaning is that, when used in a context of other iterators, that this
 * iterator is not sure if it has more data and other sources should be checked first.
 *
 * If there are no other sources with ready data, then this iterator should be assumed to have
 * no more data.
 *
 * This intentionally does not extend {@link java.util.Iterator} because this is semantically unique.
 */
public interface UncertainIterator<T> {

    enum HAS_NEXT {
        TRUE,
        FALSE,
        MAYBE
    }

    /**
     * Get the next value or throw {@link java.util.NoSuchElementException}.
     */
    T next();

    /**
     * Return TRUE if elements are ready, FALSE if they are not or MAYBE if we do not know.
     *
     * If no other sources of data are available and the state of the computation system cannot be
     * changed, then MAYBE should be considered FALSE.
     *
     * @return TRUE if elements are ready, FALSE if they are not or MAYBE if we do not know.
     */
    HAS_NEXT hasNext();

}
