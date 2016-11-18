package com.github.basking2.sdsai.sexpr.functions;

import com.github.basking2.sdsai.sexpr.EvaluationContext;
import com.github.basking2.sdsai.sexpr.SExprRuntimeException;

import java.util.Iterator;

import static com.github.basking2.sdsai.sexpr.functions.Functions.getArgument;

/**
 * A function that takes 2 arguments of given types.
 */
public abstract class AbstractFunction2<T1, T2, R> implements FunctionInterface<R> {
    @Override
    public R apply(Iterator<?> iterator, EvaluationContext evaluationContext) {

        @SuppressWarnings("unchecked") final T1 arg1 = getArgument(iterator, "1");
        @SuppressWarnings("unchecked") final T2 arg2 = getArgument(iterator, "2");

        try {
            return applyImpl(arg1, arg2, evaluationContext);
        }
        catch (final ClassCastException e) {
            throw new SExprRuntimeException("Casting argument.", e);
        }
    }

    /**
     * Overriders of this class should implement this.
     *
     * @param t1 The desired parameter.
     * @param t2 The desired parameter.
     * @param context The evaluation context.
     * @return The produced object.
     */
    protected abstract R applyImpl(T1 t1, T2 t2, EvaluationContext context);
}
