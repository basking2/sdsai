package com.github.basking2.sdsai.itrex.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A buffer iterator pulls elements from a source iterator and store N elements.
 *
 * This is useful when the down-stream may pull data more slowly form us and the up-stream can product data quickly.
 *
 * This is also useful to build a list of Futures and then consume them, though that use is not
 * intrinsic to this class's operation.
 */
public class BufferIterator<T> implements Iterator<T> {
    private int head;
    private int fill;
    private T[] items;
    private Iterator<T> source;

    public BufferIterator(final int bufferSize, final Iterator<T> source) {
        if (bufferSize < 1) {
            throw new IllegalArgumentException("Buffer size must be at least 1.");
        }

        // NOTE: We add +1 to allow us to fill the buffer before removing from the buffer.
        // If we did not +1 then the buffer would always have -1 element in it except when next() was being called.
        @SuppressWarnings("unchecked")
        final T[] t = (T[]) new Object[bufferSize + 1];

        this.source = source;
        this.items = t;
        this.head = 0;
        this.fill = 0;
    }

    @Override
    public boolean hasNext() {
        return fill > 0 || source.hasNext();
    }

    @Override
    public T next() {
        fillBuffer();
        if (! hasNext()) {
            throw new NoSuchElementException();
        }

        final T t = items[head];

        head = (head + 1) % items.length;
        fill -= 1;

        return t;
    }

    private void fillBuffer() {
        while (fill < items.length && source.hasNext()) {
            items[(head + fill) % items.length] = source.next();
            fill += 1;
        }
    }
}
