package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.EvaluationContext;

import java.util.Iterator;

/**
 * This function does nothing and returns nothing. It is useful for commenting out blocks.
 */
public class NopFunction implements FunctionInterface<Object> {
    @Override
    public Object apply(final Iterator<?> iterator, final EvaluationContext evaluationContext) {
        return null;
    }
}
