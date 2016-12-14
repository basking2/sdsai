package com.github.basking2.sdsai.itrex.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * An iterator that prefetches and caches some number of elements from another iterator.
 *
 * Sometimes it is expensive for an iterator to return a value.
 * In these situations it is sometimes preferable to pre-fetch a few objects, and
 * asynchronously replenish them.
 *
 * Order is preserved, so if there is a particularly slow fetch, it may stall future fetches.
 */
public class PrefetchingIterator<T> implements Iterator<T> {

    final private ExecutorService executorService;

    /**
     * This iterator is used to produce values and is also locked on to perform actions.
     *
     * If there are two PrefetchingIterators operating on a single iterator, this should keep
     * access to that iterator safe.
     */
    final private Iterator<T> iterator;
    final private BlockingQueue<T> queue;
    final private AtomicReference<Producer> producer = new AtomicReference<>(null);

    public PrefetchingIterator(final ExecutorService executorService, final Iterator<T> iterator, final int prefetch) {
        this.executorService = executorService;
        this.iterator = iterator;
        this.queue = new ArrayBlockingQueue<>(prefetch);
    }

    @Override
    public boolean hasNext() {
        // Queued stuff we can return without locking the iterator. We already have it.
        if (!queue.isEmpty()) {
            return true;
        }

        // This will block on the fetching thread.
        synchronized (iterator) {
            // Note: we recheck the queue contents in case it was populated while acquiring the lock.
            return (!queue.isEmpty()) || iterator.hasNext();
        }
    }

    @Override
    public T next() {

        if (queue.isEmpty()) {
            synchronized(iterator) {
                if (!iterator.hasNext()) {
                    throw new NoSuchElementException();
                }

                // Try to make a new producer.
                final Producer p = new Producer();

                // If there was no producer, set and schedule one.
                if (producer.compareAndSet(null, p)) {
                    executorService.submit(p);
                }
                else {
                    throw new NoSuchElementException();
                }
            }
        }
        while (true) {
            try {
                return queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                // NOP.
            }
        }
    }

    private class Producer implements Callable<Void> {
        @Override
        public Void call() throws Exception {
            synchronized (iterator) {
                while (queue.remainingCapacity() > 0 && iterator.hasNext()) {
                    queue.add(iterator.next());
                }

                // We are full. Signal we are done.
                producer.compareAndSet(this, null);
            }
            return null;
        }
    }
}
