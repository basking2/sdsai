/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.itrex.functions.function;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.Evaluator;
import com.github.basking2.sdsai.itrex.functions.FunctionInterface;
import com.github.basking2.sdsai.itrex.iterators.EvaluatingIterator;

import java.util.Iterator;

/**
 * A {@link FunctionInterface} that builds a function.
 */
public class FunctionFunction implements FunctionInterface<FunctionInterface<Object>> {

    private final Evaluator evaluator;

    public FunctionFunction(final Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public FunctionInterface<Object> apply(final Iterator<?> iterator, final EvaluationContext evaluationContext) {

        final Object functionBody;
        if (iterator instanceof EvaluatingIterator) {
            @SuppressWarnings("unchecked")
            final EvaluatingIterator<Object> ei = (EvaluatingIterator<Object>)iterator;
            ei.setEvaluationEnabled(false);
            functionBody = ei.next();
            ei.setEvaluationEnabled(true);
        }
        else {
            functionBody = iterator.next();
        }

        /* Build and return a function that evaluates the functionBody binding the passed arguments as
         * arguments in the evaluation context.
         */
        return (itr, ctx) ->
            evaluator.evaluate(functionBody, EvaluationContext.functionCall(ctx, itr));
    }
}
