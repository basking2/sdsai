package com.github.basking2.sdsai.itrex.functions.function;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.functions.FunctionInterface;

import java.util.Iterator;

/**
 * Fetch an argument from the current context.
 */
public class ArgFunction implements FunctionInterface<Object> {

    @Override
    public Object apply(final Iterator<?> iterator, final EvaluationContext evaluationContext) {
        return evaluationContext.getArguments().next();
    }
}
