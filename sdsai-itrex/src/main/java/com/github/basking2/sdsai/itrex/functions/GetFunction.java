/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.EvaluationContext;

import java.util.Iterator;

/**
 * Get function. ["get", "variable"].
 */
public class GetFunction extends AbstractFunction1<Object, Object> {
    @Override
    protected Object applyImpl(Object o, Iterator<?> rest, EvaluationContext context) {
        return context.get(o);
    }
}
