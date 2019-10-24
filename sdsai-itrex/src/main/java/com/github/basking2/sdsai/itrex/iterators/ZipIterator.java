package com.github.basking2.sdsai.itrex.iterators;

import com.github.basking2.sdsai.itrex.util.TwoTuple;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ZipIterator<T1, T2> implements Iterator<TwoTuple<T1, T2>> {

    private final Iterator<T1> iterator1;
    private final Iterator<T2> iterator2;
    private final boolean pad1;
    private final boolean pad2;
    private final T1 pad1Values;
    private final T2 pad2Values;

    /**
     * Zip two iterators with lots of padding options.
     * @param iterator1 The first iterator.
     * @param pad1 If the first iterator is empty, pad with a value.
     * @param pad1Values The value to pad with.
     * @param iterator2 The second iterator.
     * @param pad2 If the second iterator is empty, pad with a value.
     * @param pad2Values The value to pad with.
     */
    public ZipIterator(
            final Iterator<T1> iterator1,
            final boolean pad1,
            final T1 pad1Values,
            final Iterator<T2> iterator2,
            final boolean pad2,
            final T2 pad2Values
    ) {
        this.iterator1 = iterator1;
        this.pad1 = pad1;
        this.pad1Values = pad1Values;

        this.iterator2 = iterator2;
        this.pad2 = pad2;
        this.pad2Values = pad2Values;
    }

    /**
     * Construct a zip iterator that offers no new elements when either iterator is complete.
     * @param iterator1 The first iterator. This is not padded.
     * @param iterator2 The second iterator. This is not padded.
     */
    public ZipIterator(
            final Iterator<T1> iterator1,
            final Iterator<T2> iterator2
    ) {
        this(iterator1, false, null, iterator2, false, null);
    }

    @Override
    public boolean hasNext() {
        return
                // Both iterators have data.
                (iterator1.hasNext() && iterator2.hasNext()) ||

                // The first iterator has data and we may pad the second.
                (iterator1.hasNext() && pad2) ||

                // The second iterator has data and we may pad the second.
                (iterator2.hasNext() && pad1)
                ;
    }

    @Override
    public TwoTuple<T1, T2> next() {
        if (iterator1.hasNext()) {
            if (iterator2.hasNext()) {
                // Common path.
                return new TwoTuple<>(iterator1.next(), iterator2.next());
            }
            else if (pad2) {
               return new TwoTuple<>(iterator1.next(), pad2Values);
            }
            else {
                throw new NoSuchElementException("Iterator 1 has a next value but iterator 2 does not and may not be padded.");
            }
        }
        else if (iterator2.hasNext()) {
            if (pad1) {
                return new TwoTuple<>(pad1Values, iterator2.next());
            }
            else {
                throw new NoSuchElementException("Iterator 2 has a next value but iterator 1 does not and may not be padded.");
            }
        }
        else {
            throw new NoSuchElementException("Iterator 1 and iterator 2 do not have elements.");
        }
    }
}
