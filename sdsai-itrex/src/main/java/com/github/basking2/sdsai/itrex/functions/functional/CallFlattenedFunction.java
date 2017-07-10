package com.github.basking2.sdsai.itrex.functions.functional;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.functions.AbstractFunction1;
import com.github.basking2.sdsai.itrex.functions.FunctionInterface;
import com.github.basking2.sdsai.itrex.iterators.IteratorIterator;

import java.util.Arrays;
import java.util.Iterator;

import static com.github.basking2.sdsai.itrex.iterators.Iterators.mapIterator;

/**
 * Call a function with the remaining arguments flattened.
 *
 * So the given code fragment:
 *
 * <pre>
 *     {@code
 *     [callFlattened f [list a b] [list c d]]
 *     }
 * </pre>
 *
 * will result in calling {@code [f a b c d]}.
 */
public class CallFlattenedFunction extends AbstractFunction1<FunctionInterface<Object>, Object> {
    @Override
    protected Object applyImpl(
            final FunctionInterface<Object> objectFunctionInterface,
            final Iterator<?> rest,
            final EvaluationContext context
    ) {
        /**
         * Create an iterator that flattens the `rest` argument.
         */
        @SuppressWarnings("unchecked")
        final Iterator<Object> itrItrObj = new IteratorIterator(mapIterator(rest, e -> {
            if (e instanceof Iterator) {
                return (Iterator<Object>) e;
            }

            if (e instanceof Iterable) {
                return ((Iterable<Object>)e).iterator();
            }

            if (e instanceof Object[]) {
                return Arrays.asList((Object[])e).iterator();
            }

            return Arrays.asList((Object)e).iterator();

        }));

        // Call the function object against the flattened arguments.
        return objectFunctionInterface.apply(itrItrObj, context);
    }
}
