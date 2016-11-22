package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.SExprRuntimeException;
import com.github.basking2.sdsai.itrex.util.FutureIterator;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;

import static com.github.basking2.sdsai.itrex.util.Iterators.toIterator;

/**
 * This takes a single iterator and, for each element in that iterator, returns a single element wrapped in a Future.
 *
 * Use join to unwrap the value in that future.
 */
public class ThreadFunction implements FunctionInterface<FutureIterator<Object>> {
    private final ExecutorService executorService;

    public ThreadFunction(final ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public FutureIterator<Object> apply(final Iterator<?> iterator, final EvaluationContext evaluationContext) {
        if (!iterator.hasNext()) {
            throw new SExprRuntimeException("Thread function requires 1 argument that is an iterator or iterable.");
        }

        final Iterator<Object> objectIterator = toIterator(iterator.next());

        if (objectIterator == null) {
            throw new SExprRuntimeException("Required first argument to thread function could not be converted to an Iterator.");
        }

        @SuppressWarnings("unchecked")
        FutureIterator<Object> fi = new FutureIterator(objectIterator, executorService);

        return fi;
    }
}
