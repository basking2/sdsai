package com.github.basking2.sdsai.sexpr.functions;

import com.github.basking2.sdsai.sexpr.EvaluationContext;

import java.util.Iterator;

/**
 */
public class GetFunction implements FunctionInterface<Object> {
    @Override
    public Object apply(Iterator<?> iterator, EvaluationContext evaluationContext) {
        return evaluationContext.get(iterator.next());
    }
}
