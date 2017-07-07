package com.github.basking2.sdsai.itrex.functions.functional;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.SExprRuntimeException;
import com.github.basking2.sdsai.itrex.functions.AbstractFunction2;
import com.github.basking2.sdsai.itrex.functions.FunctionInterface;

import java.util.Iterator;

import static com.github.basking2.sdsai.itrex.iterators.Iterators.toIterator;
import static java.util.Arrays.asList;

/**
 * Fold a given value over all elements.
 *
 * <pre>
 *     {@code
 *     [* Assume the add function exists. *]
 *
 *     [foldLeft [curry add] 0 [1,2,3,4]]
 *
 *     }
 * </pre>
 *
 * This is much like the foldLeft function common in Scala or ML languages.
 *
 * There is no {@code foldRight} as iterators make it expensive to materialize to a list and then foldRight.
 * If such an algorithm is necessary, operate on the list in a reversed projection.
 */
public class FoldLeftFunction extends AbstractFunction2<FunctionInterface<Object>, Object, Object> {
    @Override
    protected Object applyImpl(
            final FunctionInterface<Object> objectFunctionInterface,
            Object o,
            final Iterator<?> rest,
            final EvaluationContext context
    ) {
        if (!rest.hasNext()) {
            throw new SExprRuntimeException("foldLeft requires a 3rd argument that is an iterable.");
        }

        final Iterator<Object> args = toIterator(rest.next());
        if (args == null) {
            throw new SExprRuntimeException("foldLeft 3rd argument cannot be converted to an iterable.");
        }

        while (args.hasNext()) {
            o = objectFunctionInterface.apply(asList(o, args.next()).iterator(), context);
        }

        return o;
    }
}
