package com.github.basking2.sdsai.itrex.iterators;

import com.github.basking2.sdsai.itrex.util.WorkStealingFuture;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * A ParallelMappingIterator will map elements in threads, using an executor service.
 */
public class ParallelMappingIterator<T, R> implements Iterator<R> {
    private final MappingIterator.Mapper<T, R> mappingFunction;
    private final ExecutorService executorService;
    private final LinkedBlockingQueue<Future<R>> queue;
    private final Iterator<T> inputs;
    private final boolean ordered;
    private final Consumer<Throwable> reportError;

    /**
     * Constructor with a default error handler.
     *
     * @param ordered If true, results from this iterator will be emitted in the same order they are read.
     *                If true, this will cause head-of-line blocking if a slow element is encountered, but
     *                often this is acceptable.
     * @param inputs The inputs to map.
     * @param executorService The executor service to dispatch work into.
     * @param breadth How many concurrent tasks should be started.
     * @param mappingFunction A function to map from type T to R.
     */
    public ParallelMappingIterator(
            final boolean ordered,
            final Iterator<T> inputs,
            final ExecutorService executorService,
            final int breadth,
            final MappingIterator.Mapper<T, R> mappingFunction
    ) {
        this(
                ordered,
                inputs,
                executorService,
                breadth,
                mappingFunction,
                t->t.printStackTrace()
        );

        fillQueue();
    }

    /**
     * Constructor.
     *
     * @param ordered If true, results from this iterator will be emitted in the same order they are read.
     *                If true, this will cause head-of-line blocking if a slow element is encountered, but
     *                often this is acceptable.
     * @param inputs The inputs to map.
     * @param executorService The executor service to dispatch work into.
     * @param breadth How many concurrent tasks should be started.
     * @param mappingFunction A function to map from type T to R.
     * @param reportError If the mapping function throws an exception, how is it handled?
     */
    public ParallelMappingIterator(
            final boolean ordered,
            final Iterator<T> inputs,
            final ExecutorService executorService,
            final int breadth,
            final MappingIterator.Mapper<T, R> mappingFunction,
            final Consumer<Throwable> reportError

    ) {
        this.ordered = ordered;
        this.inputs = inputs;
        this.mappingFunction = mappingFunction;
        this.executorService = executorService;
        this.queue = new LinkedBlockingQueue<>(breadth);
        this.reportError = reportError;

        fillQueue();
    }

    private void fillQueue() {
        while (queue.remainingCapacity() > 0 && inputs.hasNext()) {
            // Capture t in this thread.
            final T t = inputs.next();

            // Now dispatch it to be mapped.
            final Future<R> f = WorkStealingFuture.run(executorService, () -> mappingFunction.map(t));

            // And enqueue.
            try {
                queue.put(f);
            } catch (InterruptedException e) {
                // This should not happen.
                throw new RuntimeException("Unexpected exception in ParallelMappingIterator.", e);
            }
        }
    }

    @Override
    public boolean hasNext() {
        if (queue.isEmpty()) {
            return false;
        }
        else {
            return true;
        }
    }

    private Future<R> fetchHead() {
        while (true) {
            try {
                return queue.take();
            } catch (InterruptedException e) {
                // Nop - e.printStackTrace();
            }
        }
    }

    private R getResult(final Future<R> future) {
        while (true) {
            try {
                return future.get();
            } catch (final InterruptedException e) {
                // Nop - go wait again.
            } catch (final ExecutionException e) {
                reportError.accept(e);
            }
        }
    }

    private R nextOrdered() {
        final Future<R> fr = fetchHead();

        // Refill the queue.
        fillQueue();

        return getResult(fr);
    }

    private R nextUnordered() {
        for (int i = queue.size(); i > 0; i--) {
            final Future<R> fr = fetchHead();
            if (fr.isDone()) {
                // Fr is done! So take the result and move on.
                fillQueue();
                return getResult(fr);
            }
            else {
                // We just removed this from the queue, so we know there is room.
                queue.offer(fr);
            }
        }

        // If we get to the bottom, we fallback to waiting for the first element to finish.
        return nextOrdered();
    }

    @Override
    public R next() {
        // Don't let the user dead-lock themselves by waiting for elements that are never coming.
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        if (ordered) {
            return nextOrdered();
        }
        else {
            return nextUnordered();
        }
    }

}
