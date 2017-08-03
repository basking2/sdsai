package com.github.basking2.sdsai.itrex.packages;

import com.github.basking2.sdsai.itrex.Evaluator;
import com.github.basking2.sdsai.itrex.functions.FlattenFunction;
import com.github.basking2.sdsai.itrex.functions.FunctionInterface;

/**
 * This package groups functions that operate on iterators or lists.
 */
public class IteratorPackage implements Package {
    @Override
    public void importTo(final Evaluator evaluator) {
        evaluator.register("flatten", new FlattenFunction());

        importFlatten2(evaluator);
    }

    /**
     * Build and register the "flattten2" function.
     *
     * This requires the curry and flatten functions to already be registered.
     *
     * @param evaluator The evaluator to register flatten2 against.
     */
    public void importFlatten2(final Evaluator evaluator) {

        @SuppressWarnings("unchecked")
        final FunctionInterface<Object> flatten2 = (FunctionInterface<Object>)evaluator.evaluate(
                new Object[]{"curry", "callFlattened", new Object[]{"curry", "flatten"}}
        );

        evaluator.register("flatten2", flatten2);
    }
}
