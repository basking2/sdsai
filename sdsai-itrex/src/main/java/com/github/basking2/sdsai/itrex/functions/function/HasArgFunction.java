package com.github.basking2.sdsai.itrex.functions.function;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.functions.FunctionInterface;

import java.util.Iterator;

/**
 * Return True or False if the given {@link EvaluationContext} has more arguments or not.
 */
public class HasArgFunction implements FunctionInterface<Boolean> {
    @Override
    public Boolean apply(Iterator<?> iterator, EvaluationContext evaluationContext) {
        return evaluationContext.getArguments().hasNext();
    }
}
