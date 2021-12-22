/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.EvaluationContext;

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
