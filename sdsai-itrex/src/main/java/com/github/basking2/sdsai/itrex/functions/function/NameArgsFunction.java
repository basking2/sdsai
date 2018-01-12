package com.github.basking2.sdsai.itrex.functions.function;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.functions.FunctionInterface;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class NameArgsFunction implements FunctionInterface<List<Object>> {
    @Override
    public List<Object> apply(final Iterator<?> iterator, final EvaluationContext evaluationContext) {
        final List<Object> readArgs = new LinkedList<>();

        final Iterator<?> functionArguments = evaluationContext.getArguments();

        // While there are names to assign to function arguments, pull the function arguments and name them.
        while (iterator.hasNext()) {
            final Object functionParameter = iterator.next();
            if (functionArguments.hasNext()) {
                final Object functionArgument = functionArguments.next();
                evaluationContext.set(functionParameter, functionArgument);
            }
        }

        return readArgs;
    }
}
