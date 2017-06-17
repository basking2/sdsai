package com.github.basking2.sdsai.itrex.functions.function;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.Evaluator;
import com.github.basking2.sdsai.itrex.functions.FunctionInterface;

import java.util.Iterator;

/**
 * Register a function with a name.
 *
 * This is very similar to {@link FunctionFunction}, but requires
 * an additional argument, the function name to register it as.
 */
public class RegisterFunctionFunction implements FunctionInterface<FunctionInterface<Object>> {
    private final FunctionFunction f;

    public RegisterFunctionFunction(final Evaluator evaluator) {
        f = new FunctionFunction(evaluator);
    }

    @Override
    public FunctionInterface<Object> apply(Iterator<?> iterator, EvaluationContext evaluationContext) {
        final Object name = iterator.next();

        @SuppressWarnings("unchecked")
        final FunctionInterface<Object> func =  (FunctionInterface<Object>)iterator.next();

        evaluationContext.register(name, func);

        return func;
    }
}
