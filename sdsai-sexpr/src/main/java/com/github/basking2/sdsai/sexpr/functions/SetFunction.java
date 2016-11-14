package com.github.basking2.sdsai.sexpr.functions;

import com.github.basking2.sdsai.sexpr.EvaluationContext;
import com.github.basking2.sdsai.sexpr.SExprRuntimeException;

import java.util.Iterator;

/**
 * Set a value in the {@link EvaluationContext}.
 */
public class SetFunction implements FunctionInterface<Object> {
    @Override
    public Object apply(final Iterator<?> iterator, final EvaluationContext evaluationContext) {

        while (iterator.hasNext()) {
            final Object key = iterator.next();

            if (iterator.hasNext()) {
                final Object value = iterator.next();
                evaluationContext.set(key, value);
            }
            else {
                evaluationContext.set(key, null);
            }
        }

        return "Set function.";
    }
}
