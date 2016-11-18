package com.github.basking2.sdsai.sexpr.functions;

import com.github.basking2.sdsai.sexpr.EvaluationContext;

import java.util.Iterator;

public class StringConcatFunction implements FunctionInterface<String> {
    @Override
    public String apply(final Iterator<?> iterator, final EvaluationContext evaluationContext) {
        final StringBuilder sb = new StringBuilder();

        while (iterator.hasNext()) {
            sb.append(iterator.next().toString());
        }

        return sb.toString();
    }
}
