package com.github.basking2.sdsai.itrex.util;

import java.util.concurrent.*;

/**
 * A future that will, on calls to {@link #get()}, let the calling thread do the work.
 *
 * This does not prevent double-computation in all situations, but makes the risk of that very small.
 *
 * @param <T> The type returned.
 */
public class WorkStealingFuture<T> implements Future<T> {

    /**
     * The work to perform.
     */
    private final Callable<T> callable;

    /**
     * The future in the {@link ExecutorService} allowing for canceling, etc.
     */
    private Future<T> future;

    /**
     * Set when a worker thread begins computation.
     */
    private volatile boolean isStarted;

    private WorkStealingFuture(final Callable<T> callable) {
        this.callable = callable;
        this.isStarted = false;
        this.future = null;
    }

    public static <T> WorkStealingFuture<T> run(final ExecutorService executorService, final Callable<T> callable) {
        final WorkStealingFuture<T> workStealingFuture = new WorkStealingFuture<>(callable);

        workStealingFuture.future = executorService.submit(() -> {
            if (!workStealingFuture.isStarted) {
                workStealingFuture.isStarted = true;
                return callable.call();
            }
            else {
                return null;
            }
        });

        return workStealingFuture;
    }

    @Override
    public boolean cancel(boolean b) {
        return future.cancel(b);
    }

    @Override
    public boolean isCancelled() {
        return future.isCancelled();
    }

    @Override
    public boolean isDone() {
        return future.isDone();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        if (isStarted) {
            return future.get();
        }
        else {
            isStarted = true;
            try {
                return callable.call();
            } catch (Exception e) {
                throw new ExecutionException("Executing call directly.", e);
            }
        }
    }

    @Override
    public T get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(l, timeUnit);
    }
}
