/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

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
    private Future<Boolean> future;

    /**
     * Set when a worker thread begins computation by a call to {@link #tryStart()}.
     */
    private boolean isStarted;

    /**
     * Marks if the task has been completed.
     */
    private boolean isFinished;

    volatile T result;
    volatile ExecutionException resultException;

    private WorkStealingFuture(final Callable<T> callable) {
        this.callable = callable;
        this.isStarted = false;
        this.isFinished = false;
        this.future = null;
        this.result = null;
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

    /**
     * Conditionally execute the callable and notify of the results.
     *
     * This method will synchronize on {@code this} to set the {@link #isStarted} flag.
     *
     * If this call on this thread can start processing, then {@link #callable} is called
     * and the result is put in {@link #result} or if an exception was thrown {@link #resultException}.
     * The {@link #isFinished} flag is set and {@link #notifyAll()} is called.
     *
     * @return True of this call on this thread did the work.
     */
    private boolean futureTaskMethod() {

        if (tryStart()) {

            T t = null;
            ExecutionException ee = null;
            try {
                t = callable.call();
            } catch (final Throwable e) {
                ee = new ExecutionException("Executing call directly.", e);
            }

            synchronized (this) {
                this.result = t;
                this.resultException = ee;
                this.isFinished = true;
                this.notifyAll();
            }

            return true;
        }
        else {
            return false;
        }
    }

    public static <T> WorkStealingFuture<T> run(final ExecutorService executorService, final Callable<T> callable) {
        final WorkStealingFuture<T> workStealingFuture = new WorkStealingFuture<>(callable);

        workStealingFuture.future = executorService.submit(workStealingFuture::futureTaskMethod);

        return workStealingFuture;
    }

    public static <T> WorkStealingFuture<T> execute(final Executor executor, final Callable<T> callable) {
        final WorkStealingFuture<T> workStealingFuture = new WorkStealingFuture<>(callable);

        final FutureTask<Boolean> promise = new FutureTask<>(workStealingFuture::futureTaskMethod);

        workStealingFuture.future = promise;

        executor.execute(promise);

        return workStealingFuture;
    }

    /**
     * Call cancel on the underlying future.
     * @param b The boolean passed along to the underlying future's cancel method.
     * @return The result of the call to the underlying future's cancel method.
     */
    @Override
    public boolean cancel(boolean b) {
        return future.cancel(b);
    }

    /**
     * The result of calling isCancelled on the underlying future.
     * @return The result of calling isCancelled on the underlying future.
     */
    @Override
    public boolean isCancelled() {
        return future.isCancelled();
    }

    /**
     * Is this computation done and is there a result that may be fetched.
     * @return Is this computation done and is there a result that may be fetched.
     */
    @Override
    public boolean isDone() {
        return isFinished;
    }

    /**
     * If {@link #resultException} is non-null, it is thrown. Otherwise {@link #result} is returned.
     * @return The value of {@link #result}.
     * @throws ExecutionException if {@link #resultException} is not null.
     */
    private T getOrThrow() throws ExecutionException {
        if (resultException != null) {
            throw resultException;
        }
        else {
            return result;
        }
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        final boolean weDidTheWork = futureTaskMethod();

        if (weDidTheWork) {
            return getOrThrow();
        }

        else {
            // Someone else is doing the work. We must wait to be notified that it is done.
            synchronized (this) {

                // Wait until we are finished.
                while (!isFinished) {
                    this.wait();
                }

                return getOrThrow();
            }
        }
    }

    @Override
    public T get(final long l, final TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        final boolean weDidTheWork = futureTaskMethod();

        if (weDidTheWork) {
            return getOrThrow();
        }

        else {
            // Someone else is doing the work. We must wait to be notified that it is done.
            synchronized (this) {
                if (!isFinished) {
                    timeUnit.timedWait(this, l);
                }

                // Re-check if we are finished after waiting.
                if (isFinished) {
                    return getOrThrow();
                }
                else {
                    throw new TimeoutException();
                }
            }
        }
    }
}
