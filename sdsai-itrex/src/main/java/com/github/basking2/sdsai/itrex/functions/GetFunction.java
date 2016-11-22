package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.EvaluationContext;

/**
 * Get function. ["get", "variable"].
 */
public class GetFunction extends AbstractFunction1<Object, Object> {
    @Override
    protected Object applyImpl(Object o, EvaluationContext context) {
        return context.get(o);
    }
}
