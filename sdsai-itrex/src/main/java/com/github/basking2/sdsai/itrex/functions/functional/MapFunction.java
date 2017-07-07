package com.github.basking2.sdsai.itrex.functions.functional;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.SExprRuntimeException;
import com.github.basking2.sdsai.itrex.functions.AbstractFunction1;
import com.github.basking2.sdsai.itrex.functions.FunctionInterface;
import com.github.basking2.sdsai.itrex.iterators.MappingIterator;

import java.util.ArrayList;
import java.util.Iterator;

import static com.github.basking2.sdsai.itrex.iterators.Iterators.toIterator;
import static com.github.basking2.sdsai.itrex.iterators.Iterators.wrap;

/**
 * Map a function across all subsequent arguments.
 */
public class MapFunction extends AbstractFunction1<FunctionInterface<Object>, Iterator<Object>> {
    @Override
    protected Iterator<Object> applyImpl(final FunctionInterface<Object> function, final Iterator<?> rest, final EvaluationContext evaluationContext) {
        if (!rest.hasNext()) {
            throw new SExprRuntimeException("Map function requires a second argument.");
        }
        final Object iteratorObject = rest.next();

        final Iterator<Object> returnIterator = toIterator(iteratorObject);
        if (returnIterator == null) {
            throw new SExprRuntimeException("Second argument must be an iterator.");
        }

        return new MappingIterator<Object, Object>(returnIterator, o -> function.apply(wrap(o), evaluationContext));
    }
}
