package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.SExprRuntimeException;
import com.github.basking2.sdsai.itrex.util.FutureIterator;

import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import static com.github.basking2.sdsai.itrex.util.Iterators.toIterator;

/**
 * This takes a single iterator and, for each element in that iterator, returns a single element wrapped in a Future.
 *
 * Use join to unwrap the value in that future.
 */
public class ThreadFunction implements FunctionInterface<Iterator<Future<Object>>> {
    private final Executor executor;

    public ThreadFunction(final Executor executor) {
        this.executor= executor;
    }

    @Override
    public Iterator<Future<Object>> apply(final Iterator<?> iterator, final EvaluationContext evaluationContext) {
        if (!iterator.hasNext()) {
            throw new SExprRuntimeException("Thread function requires 1 argument that is an iterator or iterable.");
        }

        final Iterator<Object> objectIterator = toIterator(iterator.next());

        if (objectIterator == null) {
            throw new SExprRuntimeException("Required first argument to thread function could not be converted to an Iterator.");
        }

        final FutureIterator<Object> fi = new FutureIterator<Object>(objectIterator, executor);

        return fi;
    }
}
