/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.EvaluationContext;

import java.util.Iterator;

/**
 * Set a value in the {@link EvaluationContext}.
 */
public class UpdateFunction extends AbstractFunction2<Object, Object, Object> {
    @Override
    protected Object applyImpl(Object o, Object o2, Iterator<?> rest, EvaluationContext context) {
        context.update(o, o2);
        return o2;
    }
}
