package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.SExprRuntimeException;

import java.util.Iterator;

import static com.github.basking2.sdsai.itrex.functions.Functions.getArgument;

/**
 * A function that takes 1 argument of a given type.
 */
public abstract class AbstractFunction3<T1, T2, T3, R> implements FunctionInterface<R> {
    @Override
    public R apply(final Iterator<?> iterator, final EvaluationContext evaluationContext) {
        final T1 arg1 = getArgument(iterator, "1");
        final T2 arg2 = getArgument(iterator, "2");
        final T3 arg3 = getArgument(iterator, "3");

        try {
            return applyImpl(arg1, arg2, arg3, evaluationContext);
        }
        catch (final ClassCastException e) {
            throw new SExprRuntimeException("Casting argument.", e);
        }
    }

    /**
     * @param arg1 The first argument.
     * @param arg2 The second argument.
     * @param arg3 The third argument.
     * @param context The evaluation context.
     * @return The result.
     */
    protected abstract R applyImpl(T1 arg1, T2 arg2, T3 arg3, EvaluationContext context);
}
