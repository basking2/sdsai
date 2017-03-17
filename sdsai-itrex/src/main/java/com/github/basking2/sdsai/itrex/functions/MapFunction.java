package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.SExprRuntimeException;
import com.github.basking2.sdsai.itrex.iterators.MappingIterator;

import java.util.ArrayList;
import java.util.Iterator;

import static com.github.basking2.sdsai.itrex.iterators.Iterators.toIterator;
import static com.github.basking2.sdsai.itrex.iterators.Iterators.wrap;

/**
 * Map a function across all subsequent arguments.
 */
public class MapFunction implements FunctionInterface<Iterator<Object>> {
    @SuppressWarnings("unchecked")
    @Override
    public Iterator<Object> apply(final Iterator<? extends Object> objectIterator, final EvaluationContext evaluationContext) {

        // If no function, then no results. No worries.
        if (!objectIterator.hasNext()) {
            return new ArrayList<Object>().iterator();
        }

        final Object functionObject = objectIterator.next();
        if (!(functionObject instanceof FunctionInterface)) {
            throw new SExprRuntimeException("First argument to map must be a function.");
        }

        final FunctionInterface<Object> function = (FunctionInterface<Object>)(functionObject);

        if (!objectIterator.hasNext()) {
            throw new SExprRuntimeException("Map function requires a second argument.");
        }
        final Object iteratorObject = objectIterator.next();

        final Iterator<Object> returnIterator = toIterator(iteratorObject);
        if (returnIterator == null) {
            throw new SExprRuntimeException("Second argument must be an iterator.");
        }

        return new MappingIterator<Object, Object>(returnIterator, o -> function.apply(wrap(o), evaluationContext));
    }
}
