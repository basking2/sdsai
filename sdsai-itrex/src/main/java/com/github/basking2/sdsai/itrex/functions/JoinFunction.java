package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.SExprRuntimeException;

import java.util.Iterator;
import java.util.concurrent.Future;

import static com.github.basking2.sdsai.itrex.util.Iterators.mapIterator;
import static com.github.basking2.sdsai.itrex.util.Iterators.toIterator;

/**
 * Join any Future object from a given array.
 *
 * {@code ["join", ["thread", ["list"...]]]}
 */
public class JoinFunction implements FunctionInterface<Iterator<Object>> {

    @Override
    public Iterator<Object> apply(Iterator<?> iterator, EvaluationContext evaluationContext) {
        if (!iterator.hasNext()) {
            throw new SExprRuntimeException("Join function requires a single argument that is an iterator or iterable.");
        }

        final Iterator<Future<Object>> objectIterator = toIterator(iterator.next());

        if (objectIterator == null) {
            throw new SExprRuntimeException("Join function could not convert its argument to an iterator.");
        }

        return mapIterator(objectIterator, e -> e.get());
    }
}
