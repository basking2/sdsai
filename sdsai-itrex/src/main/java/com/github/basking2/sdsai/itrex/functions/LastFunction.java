/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.EvaluationContext;

import java.util.Iterator;

/**
 * Evaluate all arguments, but only return the value of the last element.
 */
public class LastFunction implements FunctionInterface<Object> {
    
    @Override
    public Object apply(final Iterator<? extends Object> objectIterator, final EvaluationContext evaluationContext) {
        Object o = null;

        while (objectIterator.hasNext()) {
            o = objectIterator.next();
        }

        return o;
    }

}
