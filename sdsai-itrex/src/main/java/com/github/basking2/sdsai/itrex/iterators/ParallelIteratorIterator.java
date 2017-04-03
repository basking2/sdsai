package com.github.basking2.sdsai.itrex.iterators;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

        // At this point we know there are pending results, we just need to get them.
        while (!hasResult) {

            // 1. Recheck that there is an element to be had.
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            
            // 2. Try to take 1 from any actor.
            //    This only fails if all the actors are currently processing a result in a thread.
            for (final Actor a : actors) {
                try {
                    t = a.tryNext();
                    if (t != null) {
                        scheduleDeferedActors();
                        return t;
                    }
                }
                catch (final NoSuchElementException nse) {
                    // Nop
                }
            }

            // Step 1. tells us there is an element to be had.
            // Step 2. tells us all actors are in threads generating an element.
            // 3. We may safely just wait for the result.
            try {
                scheduleDeferedActors();
                return resultsQueue.take();
            }
            catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }

        return t;
    }

    /**
     * Reschedule actors that have gone to sleep.
     */
    private void scheduleDeferedActors() {
        // Always restart when we call next.
        for (int i = deferQueue.size(); i > 0; i--) {
            executor.execute(deferQueue.poll());
        }
    }

    /**
     * A {@link Runnable} that owns a particular {@link Iterator}.
     */
    private class Actor implements Runnable {

        /**
         * A lock to explicitly protect access to the {@link #iterator}.
         *
         * This lock also, when locked, signals that this Actor (or the owning ParallelIteratorIterator) is
         * in a thread doing work. If this is locked, a result will be generated.
         */
        private Lock lock;

        private Iterator<T> iterator;

        public Actor(final Iterator<T> iterator) {
            this.iterator = iterator;
            this.lock = new ReentrantLock();
        }

        /**
         * Try to fill the {@link #resultsQueue} with as many elements as will fit leaving some headroom.
         */
        @Override
        public void run() {
            
            boolean hasNext = true;

            // While we can fetch and place a result, do so.
            while (hasNext && resultsQueue.remainingCapacity() >= actors.size()) {

                // On each iteration collect if this iterator has more elements, and if yes, the element.
                lock.lock();
                try {
                    hasNext = iterator.hasNext();
                    if (hasNext) {
                        resultsQueue.add(iterator.next());
                    }
                }
                catch (final Throwable t) {
                    // This should not happen, but report if it does.
                    t.printStackTrace();
                }
                finally {
                    lock.unlock();
                }
            }

            // If we are here and the iterator has more elements, there is more work to do.
            // So we go on the defer queue. We'll be rescheduled later.
            if (hasNext) {
                deferQueue.add(this);
            }
        }
        
        /**
         * Try to lock and fetch the next value from this actor's iterator.
         *
         * @return Null if unable to lock, the value otherwise.
         */
        public T tryNext() {
            if (lock.tryLock()) {
                try {
                    return iterator.next();
                }
                finally {
                    lock.unlock();
                }
            }
            else {
                return null;
            }
        }

        public boolean hasNext() {
            lock.lock();
            try {
                return iterator.hasNext();
            }
            finally {
                lock.unlock();
            }
        }
    }
}
