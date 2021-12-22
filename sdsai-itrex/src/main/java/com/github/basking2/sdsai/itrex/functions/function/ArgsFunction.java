/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.itrex.functions.function;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.functions.FunctionInterface;

import java.util.Iterator;

/**
 * Fetch all arguments from the current context.
 */
public class ArgsFunction implements FunctionInterface<Iterator<?>> {

    @Override
    public Iterator<?> apply(final Iterator<?> iterator, final EvaluationContext evaluationContext) {
        return evaluationContext.getArguments();
    }
}
