/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.SExprRuntimeException;

import java.util.Iterator;

import static com.github.basking2.sdsai.itrex.functions.Functions.getArgument;

/**
 * A function that takes 1 argument of a given type.
 */
public abstract class AbstractFunction1<T, R> implements FunctionInterface<R> {
    @Override
    public R apply(final Iterator<?> iterator, final EvaluationContext evaluationContext) {

        final T arg1 = getArgument(iterator, "1");

        try {
            return applyImpl(arg1, iterator, evaluationContext);
        }
        catch (final ClassCastException e) {
            throw new SExprRuntimeException("Casting argument.", e);
        }
    }

    /**
     * Overriders of this class should implement this.
     *
     * @param t The desired parameter.
     * @param rest Any unused arguments.
     * @param context The evaluation context.
     * @return The produced object.
     */
    protected abstract R applyImpl(T t, Iterator<?> rest, EvaluationContext context);
}
