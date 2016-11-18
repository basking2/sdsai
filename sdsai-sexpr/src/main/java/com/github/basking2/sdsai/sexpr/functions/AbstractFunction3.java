package com.github.basking2.sdsai.sexpr.functions;

import com.github.basking2.sdsai.sexpr.EvaluationContext;
import com.github.basking2.sdsai.sexpr.SExprRuntimeException;

import java.util.Iterator;

import static com.github.basking2.sdsai.sexpr.functions.Functions.getArgument;

/**
 * A function that takes 1 argument of a given type.
 */
public abstract class AbstractFunction3<T1, T2, T3, R> implements FunctionInterface<R> {
    @Override
    public R apply(final Iterator<?> iterator, final EvaluationContext evaluationContext) {
        @SuppressWarnings("unchecked") final T1 arg1 = getArgument(iterator, "1");
        @SuppressWarnings("unchecked") final T2 arg2 = getArgument(iterator, "2");
        @SuppressWarnings("unchecked") final T3 arg3 = getArgument(iterator, "3");

        try {
            return applyImpl(arg1, arg2, arg3, evaluationContext);
        }
        catch (final ClassCastException e) {
            throw new SExprRuntimeException("Casting argument.", e);
        }
    }

    protected abstract R applyImpl(T1 arg1, T2 arg2, T3 arg3, EvaluationContext context);
}
