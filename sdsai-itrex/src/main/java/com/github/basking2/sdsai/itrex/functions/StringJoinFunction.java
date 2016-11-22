package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.EvaluationContext;

import java.util.Iterator;

public class StringJoinFunction implements FunctionInterface<String> {
    @Override
    public String apply(final Iterator<?> iterator, final EvaluationContext evaluationContext) {
        if (!iterator.hasNext()) {
            return "";
        }

        final String s = iterator.next().toString();

        final StringBuilder sb = new StringBuilder();

        while (iterator.hasNext()) {
            sb.append(iterator.next().toString()).append(s);
        }

        return sb.substring(0, sb.length() - s.length());
    }
}
