package com.github.basking2.sdsai.itrex.util;

import java.util.concurrent.*;

/**
 * A future that will, on calls to {@link #get()}, let the calling thread do the work.
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

    /**
     * Lock this object and try to set isStarted to true.
     *
     * @return True if isStarted was not previously set to true and this function call set it to true, false otherwise.
     */
    private boolean tryStart() {
        synchronized (this) {
            if (isStarted) {
                return false;
            }
            else {
                isStarted = true;
                return true;
            }
        }
    }

    public static <T> WorkStealingFuture<T> run(final ExecutorService executorService, final Callable<T> callable) {
        final WorkStealingFuture<T> workStealingFuture = new WorkStealingFuture<>(callable);

        workStealingFuture.future = executorService.submit(() -> {
            if (workStealingFuture.tryStart()) {
                return callable.call();
            }
            else {
                return null;
            }
        });

        return workStealingFuture;
    }

    public static <T> WorkStealingFuture<T> execute(final Executor executor, final Callable<T> callable) {
        final WorkStealingFuture<T> workStealingFuture = new WorkStealingFuture<>(callable);

        final FutureTask<T> promise = new FutureTask<>(() -> {
            if (workStealingFuture.tryStart()) {
                return callable.call();
            }
            else {
                return null;
            }
        });

        workStealingFuture.future = promise;

        executor.execute(promise);

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
        if (tryStart()) {
            try {
                return callable.call();
            } catch (final Throwable e) {
                throw new ExecutionException("Executing call directly.", e);
            }
        }
        else {
            return future.get();
        }
    }

    @Override
    public T get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(l, timeUnit);
    }
}
