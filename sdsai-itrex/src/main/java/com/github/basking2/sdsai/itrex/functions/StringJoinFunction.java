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
            final Object o = iterator.next();
            if (o instanceof Iterator) {
                @SuppressWarnings("unchecked")
                final Iterator<Object> i = (Iterator<Object>)o;
                while (i.hasNext()) {
                    sb.append(i.next().toString()).append(s);
                }
            }
            else {
                sb.append(o.toString()).append(s);
            }
        }

        return sb.delete(sb.length() - s.length(), sb.length()).toString();
    }
}
