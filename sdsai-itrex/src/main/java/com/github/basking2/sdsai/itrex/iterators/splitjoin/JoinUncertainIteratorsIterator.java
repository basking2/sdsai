package com.github.basking2.sdsai.itrex.iterators.splitjoin;

import com.github.basking2.sdsai.itrex.util.WorkStealingFuture;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Collect and present data from a set of {@link UncertainIterator}s.
 *
 * Every iterator with available data will be scheduled to fetch its in the given ExecutorService.
 * Thus this job processes, roughly, as widely as the distribution of iterator data.
 *
 * If every {@link UncertainIterator} returns {@link com.github.basking2.sdsai.itrex.iterators.splitjoin.UncertainIterator.HAS_NEXT#MAYBE}
 * then this iterator will report false from {@link #hasNext()}. The processing model is that of the set of input
 * iterators, at least 1 will have some work to do at all times. This is suitable for distributing work in which
 * we always know that some work is available, but we do not know where it comes from.
 *
 * @param <T> The types being joined.
 */
public class JoinUncertainIteratorsIterator<T> implements Iterator<T> {

    /**
     * Where iterators come from.
     */
    private final UncertainIterator<UncertainIterator<T>> uncertainIteratorUncertainIterator;

    /**
     * Iterators that are not known to be empty. Pool them for data.
     */
    private final List<UncertainIterator<T>> liveIterators;

    /**
     * Iterators from the liveIterators list that are in process of computing in the executor.
     */
    private final List<UncertainIterator<T>> workingIterators;

    /**
     * The results returned by an iterator in the workingIterators list.
     */
    private final List<Future<T>> workResults;

    private final ExecutorService executorService;


    public JoinUncertainIteratorsIterator(final ExecutorService executorService, final UncertainIterator<UncertainIterator<T>> iterators) {
        this.liveIterators = new ArrayList<>();
        this.uncertainIteratorUncertainIterator = iterators;
        this.workResults = new ArrayList();
        this.workingIterators = new ArrayList();
        this.executorService = executorService;
    }

    public JoinUncertainIteratorsIterator(final ExecutorService executorService, final List<UncertainIterator<T>> liveIterators) {
        this.liveIterators = liveIterators;
        this.workResults = new ArrayList();
        this.workingIterators = new ArrayList();
        this.executorService = executorService;
        this.uncertainIteratorUncertainIterator = new UncertainIterator<UncertainIterator<T>>() {
            @Override
            public UncertainIterator<T> next() {
                throw new NoSuchElementException();
            }

            @Override
            public HAS_NEXT hasNext() {
                return HAS_NEXT.FALSE;
            }
        };
    }

    public static <T> JoinUncertainIteratorsIterator<T> join(final ExecutorService executorService, final UncertainIterator<UncertainIterator<T>> uncertainIteratorUncertainIterator) {
        return new JoinUncertainIteratorsIterator<>(executorService, uncertainIteratorUncertainIterator);
    }

    public static <T> JoinUncertainIteratorsIterator<T> joinList(final ExecutorService executorService, final List<UncertainIterator<T>> list) {
        return new JoinUncertainIteratorsIterator<>(executorService, list);
    }

    public static <T> JoinUncertainIteratorsIterator<T> joinSingle(final ExecutorService executorService, final UncertainIterator<T> uncertainIterator) {
        final List<UncertainIterator<T>> list = new ArrayList<>();
        list.add(uncertainIterator);
        return new JoinUncertainIteratorsIterator<>(executorService, list);
    }

    /**
     * Find iterators in the live list that should go in the work list because the have data waiting to fetched.
     */
    private void fillWorkQueue() {
        // First, fill the live list with any new iterators.
        // All iterators have at least 1 element waiting.
        while (uncertainIteratorUncertainIterator.hasNext() == UncertainIterator.HAS_NEXT.TRUE) {
            final UncertainIterator<T> ui = uncertainIteratorUncertainIterator.next();
            switch (ui.hasNext()) {
                case TRUE:
                    // It has data! Remove from the live list AND start it working.
                    final Future<T> future = WorkStealingFuture.run(executorService, () -> ui.next());
                    workingIterators.add(ui);
                    workResults.add(future);
                    break;
                case MAYBE:
                    // We should really never get here, but we cover the case in a sane way.
                    liveIterators.add(ui);
                    break;
                case FALSE:
                    // This iterator is empty and should be dropped.
                    break;
            }
        }

        // A list of iterators to remove from the live list because they are expired or scheduled in the work list.
        final List<UncertainIterator<T>> toRemove = new ArrayList<>();

        // Now try to move all iterators from the live list to the working list...
        for (final UncertainIterator<T> ui : liveIterators) {
            switch (ui.hasNext()) {
                case TRUE:
                    // It has data! Remove from the live list AND start it working.

                    final Future<T> future = WorkStealingFuture.run(executorService, () -> ui.next());
                    workingIterators.add(ui);
                    workResults.add(future);
                    toRemove.add(ui);
                    break;
                case FALSE:
                    // It's empty! Just remove it.
                    toRemove.add(ui);
                    break;
                case MAYBE:
                    // Nop - leave it here.
                    break;
            }
        }

        liveIterators.removeAll(toRemove);
    }

    @Override
    public boolean hasNext() {
        // Do a simple check before calling fillWorkQueue()
        // FillWorkQueue, as a side effect, enqueues elements.
        if (!workResults.isEmpty()) {
            return true;
        }

        fillWorkQueue();

        if (!workResults.isEmpty()) {
            return true;
        }

        return false;
    }

    /**
     * Move an iterator from the work list back to the live list. Call fillWorkQueue() to see if we can move it right
     * back into the work list and enqueue other pending iterators to work.
     *
     * @param indexInWorkList The index into {@link #workingIterators} and {@link #workResults} to process.
     * @return A mapped element.
     */
    private T putIteratorBackInTheLiveList(final int indexInWorkList) {
        liveIterators.add(workingIterators.remove(indexInWorkList));
        final Future<T> f = workResults.remove(indexInWorkList);

        while (true) {
            try {
                final T t = f.get();
                fillWorkQueue();
                return t;
            } catch (final InterruptedException e) {
                // Nop - we'll try again.
            } catch (final ExecutionException e) {
                e.printStackTrace();
                throw new NoSuchElementException("Executor Exception: " + e.getMessage());
            }
        }
    }

    @Override
    public T next() {

        // Protect against the user not calling hasNext() (though they should).
        if (workResults.isEmpty()) {
            fillWorkQueue();
            if (workResults.isEmpty()) {
                throw new NoSuchElementException();
            }
        }

        // Search for a done future.
        for (int i = workResults.size()-1; i >= 0; --i) {
            final Future<T> future = workResults.get(i);
            if (future.isDone()) {

                return putIteratorBackInTheLiveList(i);

            }
        }

        // If no future is done, just grab one and call get() on it. Because we use WorkStealingFutures
        // if the job isn't on a thread yet, we'll do it.
        // Note - take the last element since it requires the least element moving in an array list.
        return putIteratorBackInTheLiveList(workingIterators.size() -1);
    }
}
