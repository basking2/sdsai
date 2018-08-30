package com.github.basking2.sdsai.itrex.iterators.splitjoin;

import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * An uncertain iterator that may be closed, signalling it will receive or generate no more elements.
 */
public class CloseableUncertainIterator<T> implements UncertainIterator<T>, AutoCloseable {

    private final Queue<T> queue;
    private volatile boolean closed;

    /**
     * Construct this using the given queue for data.
     *
     * When the queue has data {@link #hasNext()} will reeturn {@link UncertainIterator.HAS_NEXT#TRUE}.
     *
     * When the queue is empty {@link #hasNext()} will return {@link UncertainIterator.HAS_NEXT#MAYBE}.
     *
     * When the queue is empty and this object has been {@link #close()}ed, then {@link UncertainIterator.HAS_NEXT#FALSE}
     * is returned.
     *
     * @param queue The queue to read elements from.
     */
    public CloseableUncertainIterator(final Queue<T> queue) {
        this.queue = queue;
        this.closed = false;
    }

    /**
     * Calls {@link #CloseableUncertainIterator(Queue)} with a {@link ConcurrentLinkedQueue} instance.
     */
    public CloseableUncertainIterator() {
        this(new ConcurrentLinkedQueue<>());
    }

    @Override
    public T next() {
        if (queue.isEmpty()) {
            throw new NoSuchElementException("Input queue is empty.");
        }

        return queue.poll();
    }

    @Override
    public HAS_NEXT hasNext() {
        if (queue.isEmpty()) {
            if (closed) {
                return HAS_NEXT.FALSE;
            }
            else {
                return HAS_NEXT.MAYBE;
            }
        }

        return HAS_NEXT.TRUE;
    }

    @Override
    public void close() throws Exception {
        closed = true;
    }

    /**
     * This calls {@link Queue#offer(Object)} on the backing queue and returns the result.
     *
     * If the user would like to use custom enqueuing semantics they should provide their own thread-safe
     * queue to the constructor.
     *
     * @param t The element to enqueue.
     * @return True on success, false otherwise.
     */
    public boolean offer(final T t) {
        return queue.offer(t);
    }
}
