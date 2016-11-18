package com.github.basking2.sdsai.sexpr.functions;

import com.github.basking2.sdsai.sexpr.EvaluationContext;

import java.util.Iterator;

/**
 * Get function. ["get", "variable"].
 */
public class GetFunction extends AbstractFunction1<Object, Object> {
    @Override
    protected Object applyImpl(Object o, EvaluationContext context) {
        return context.get(o);
    }
}
