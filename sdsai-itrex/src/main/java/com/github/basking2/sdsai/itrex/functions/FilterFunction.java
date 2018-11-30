package com.github.basking2.sdsai.itrex.functions;

import java.util.Arrays;
import java.util.Iterator;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.SExprRuntimeException;
import com.github.basking2.sdsai.itrex.iterators.Iterators;

import static com.github.basking2.sdsai.itrex.iterators.Iterators.filterIterator;

/**
 * Filter an iterable using a {@link com.github.basking2.sdsai.itrex.iterators.FilterIterator}.
 */
public class FilterFunction implements FunctionInterface<Iterator<?>> {

    @Override
    public Iterator<?> apply(final Iterator<?> iterator, final EvaluationContext evaluationContext) {
        if (!iterator.hasNext()) {
            throw new SExprRuntimeException("Filter function requires 2 arguments but got none.");
        }

        final Object predicateObject = iterator.next();

        if (! (predicateObject instanceof FunctionInterface)) {
            throw new SExprRuntimeException("Filter function requires its first argument be a predicate.");
        }

        final FunctionInterface<Boolean> predicate = (FunctionInterface<Boolean>)predicateObject;

        if (!iterator.hasNext()) {
            throw new SExprRuntimeException("Filter function requires its second argument but only found one.");
        }

        final Iterator<Object> input = Iterators.toIterator(iterator.next());

        if (input == null) {
            throw new SExprRuntimeException("Filter function requires its second argument to be convertable to an interator.");
        }

        return filterIterator(input, (e) -> predicate.apply(Arrays.asList(e).iterator(), evaluationContext));
    }
}
