package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.EvaluationContext;

/**
 * Set a value in the {@link EvaluationContext}.
 */
public class SetFunction extends AbstractFunction2<Object, Object, Object> {
    @Override
    protected Object applyImpl(Object o, Object o2, EvaluationContext context) {
        context.set(o, o2);
        return o2;
    }
}
