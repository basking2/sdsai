package com.github.basking2.sdsai.sexpr.functions;

import com.github.basking2.sdsai.sexpr.EvaluationContext;
import com.github.basking2.sdsai.sexpr.SExprRuntimeException;

import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.github.basking2.sdsai.sexpr.util.Iterators.mapIterator;

/**
 * Join any Future object from a given array.
 *
 * {@code ["join", ["thread", ["list"...]]]}
 */
public class JoinFunction extends AbstractFunction1<Iterator<Future<Object>>, Iterator<Object>> {

    @Override
    protected Iterator<Object> applyImpl(final Iterator<Future<Object>> objectFuture, EvaluationContext context) {
        return mapIterator(objectFuture, e -> e.get());
    }
}
