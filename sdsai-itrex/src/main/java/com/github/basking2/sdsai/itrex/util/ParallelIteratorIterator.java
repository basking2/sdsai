package com.github.basking2.sdsai.itrex.util;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

/**
 * Fetch from a set of iterators concurrently and return the first ready element.
 *
 * Similar to {@link IteratorIterator}, this aggregates a set of iterators.
 */
public class ParallelIteratorIterator<T> implements Iterator<T> {

    final List<Actor> actors;
    final private Executor executor;
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
     * Constructor.
     *
     * @param executor The executor that provide the parallelism.
     * @param queueSize The depth of the queue to use for iterated results.
     *                  For performance reasons the actual depth of cached results
     *                  is the length of the inputs + queueSize. The reason for this is that
     *                  in some very contrived timing situations all iterators can begin 
     *                  producing values and exhaust the last queue space. Typically only 1
     *                  producing iterator will do this.
     * @param inputs The list of input iterators. Each iterator may be accessed in parallel with another.
     *               If iterators share resources, this behavior must be accounted for.
     */
    public ParallelIteratorIterator(
            final Executor executor,
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
        this.executor = executor;
        this.resultsQueue = new ArrayBlockingQueue<>(queueSize + actors.size());
        this.deferQueue = new ArrayBlockingQueue<>(actors.size());

        // Start all actors.
        for (final Actor a : actors) {
            executor.execute(a);
        }
    }

    /**
     * Check if a new result should be expected and fetch with a call to {@link #next()}.
     * 
     * This first checks if the internal queue has a cached result, and returns true if it does.
     * 
     * Otherwise, each actor is checked if it still has results to fetch. If any returns true, this returns
     * true.
     * 
     * Finally, the results queue is re-checked in case an actor published a result between the time we
     * first checked the results queue and then checked each actor individually.
     * 
     * @return True if there are more elements to fetch or retrieve out of the cache queue. False otherwise.
     */
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
            executor.execute(deferQueue.poll());
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

    private class Actor implements Runnable {
        private Iterator<T> iterator;

        public Actor(final Iterator<T> iterator) {
            this.iterator = iterator;
        }

        @Override
        public void run() {

            boolean hasNext = true;

            // While we can fetch and place a result, do so.
            while (hasNext && resultsQueue.remainingCapacity() >= actors.size()) {

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
        }

        public boolean hasNext() {
            synchronized (iterator) {
                return iterator.hasNext();
            }
        }
    }
}
