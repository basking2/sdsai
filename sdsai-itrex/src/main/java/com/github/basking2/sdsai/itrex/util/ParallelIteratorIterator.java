package com.github.basking2.sdsai.itrex.util;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * Fetch from a set of iterators concurrently and return the first ready element.
 *
 * Similar to {@link IteratorIterator}, this aggregates a set of iterators.
 */
public class ParallelIteratorIterator<T> implements Iterator<T> {

    final List<Actor> actors;
    final private ExecutorService executorService;
    final private BlockingQueue<Actor> deferQueue;

    /**
     * Holds the results of iteration.
     *
     * This must be {@link #actors} in length or more. Producers will not start fetching a new element
     * unless there is N capacity (where N is the number of iterators). This ensures that
     * no producer will take the last spaces available.
     *
     * This does man that N extra elements may be fetched under very precise timing conditions.
     */
    final private BlockingQueue<T> resultsQueue;

    /**
     *
     * @param executorService
     * @param queueSize
     * @param inputs
     */
    public ParallelIteratorIterator(
            final ExecutorService executorService,
            final int queueSize,
            final List<Iterator<T>> inputs
    )
    {
        final List<Actor> actorsTmp = new ArrayList<>(inputs.size());

        // Schedule all iterators to populate the results queue.
        for (final Iterator<T> i : inputs) {

            // Never schedule an empty iterator for work.
            if (i.hasNext()) {
                actorsTmp.add(new Actor(i));
            }
        }

        // Build a results queue that can hold 1 result from ever iterator, or more.
        this.actors = Collections.unmodifiableList(actorsTmp);
        this.executorService = executorService;
        this.resultsQueue = new ArrayBlockingQueue<>(queueSize + actors.size());
        this.deferQueue = new ArrayBlockingQueue<>(actors.size());

        // Start all actors.
        for (final Actor a : actors) {
            executorService.submit(a);
        }
    }

    @Override
    public boolean hasNext() {
        // This is fast, so we opt to try this first.
        if (!resultsQueue.isEmpty()) {
            return true;
        }

        // If there are no results in the queue, check the actors. Are there any pending results?
        for (final Actor a : actors) {
           if (a.hasNext()) {
               return true;
           }
        }

        // If the producers (actors) are empty, recheck the results queue.
        return !resultsQueue.isEmpty();
    }

    @Override
    public T next() {
        T t;
        boolean hasResult;

        if (!resultsQueue.isEmpty()) {
            t = resultsQueue.poll();
            hasResult = true;
        }
        else {
            t = null;
            hasResult = false;
        }

        // Always restart when we call next.
        while (!deferQueue.isEmpty()) {
            executorService.submit(deferQueue.poll());
        }

        // If we don't have a result...
        while (!hasResult) {

            // recheck that there is an element to be had and then wait.
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            try {
                return resultsQueue.take();
            }
            catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }

        return t;
    }

    private class Actor implements Callable<Void> {
        private Iterator<T> iterator;

        public Actor(final Iterator<T> iterator) {
            this.iterator = iterator;
        }

        @Override
        public Void call() throws Exception {

            boolean hasNext = true;

            // While we can fetch and place a result, do so.
            while (resultsQueue.remainingCapacity() >= actors.size()) {

                // On each iteration collect if this iterator has more elements, and if yes, the element.
                synchronized (iterator) {
                    hasNext = iterator.hasNext();
                    if (hasNext) {
                        resultsQueue.add(iterator.next());
                    }
                }
            }

            // If we are here and the iterator has more elements, there is more work to do.
            // So we go on the defer queue. We'll be rescheduled later.
            if (hasNext) {
                deferQueue.add(this);
            }

            return null;
        }

        public boolean hasNext() {
            synchronized (iterator) {
                return iterator.hasNext();
            }
        }
    }
}
