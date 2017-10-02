package com.github.basking2.sdsai.itrex.functions.function;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.Evaluator;
import com.github.basking2.sdsai.itrex.SExprRuntimeException;
import com.github.basking2.sdsai.itrex.functions.FunctionInterface;
import com.github.basking2.sdsai.itrex.iterators.EvaluatingIterator;

import java.util.Iterator;

/**
 * A handy utility function that defines a function or retrieves a definition of one.
 *
 * The first argument to this is always a function name.
 * The optional second argument is a function body that will be registered under the given name.
 * If no function body is given, the previously defined function is looked up or an exception is thrown.
 */
public class FnFunction implements FunctionInterface<FunctionInterface<?>> {
    private final Evaluator evaluator;

    public FnFunction(final Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public FunctionInterface<?> apply(Iterator<?> iterator, EvaluationContext evaluationContext) {
        final Object functionName = iterator.next();
        final FunctionInterface<?> functionImpl;

        if (iterator.hasNext()) {
            final Object functionBody;
            if (iterator instanceof EvaluatingIterator) {
                @SuppressWarnings("unchecked")
                final EvaluatingIterator<Object> ei = (EvaluatingIterator<Object>)iterator;
                ei.setEvaluationEnabled(false);
                functionBody = ei.next();
                ei.setEvaluationEnabled(true);
            }
            else {
                functionBody = iterator.next();
            }

            functionImpl = (args, ctx) -> evaluator.evaluate(functionBody, new EvaluationContext(ctx, args));

            evaluationContext.register(functionName, functionImpl);

        }
        else {
            functionImpl = evaluationContext.getFunction(functionName);

            if (functionImpl == null) {
                throw new SExprRuntimeException("Function " + functionName + " not defined.");
            }
        }

        return functionImpl;
    }
}
