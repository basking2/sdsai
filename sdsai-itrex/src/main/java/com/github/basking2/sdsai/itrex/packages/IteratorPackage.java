/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.itrex.packages;

import com.github.basking2.sdsai.itrex.Evaluator;
import com.github.basking2.sdsai.itrex.functions.FlattenFunction;
import com.github.basking2.sdsai.itrex.functions.FunctionInterface;

/**
 * This package groups functions that operate on iterators or lists.
 */
public class IteratorPackage implements Package {
    @Override
    public void importTo(final Evaluator evaluator, final String packageName) {
        if (packageName == null || packageName.isEmpty()) {
            evaluator.register("flatten", new FlattenFunction());
        }
        else {
            evaluator.register(packageName + ".flatten", new FlattenFunction());
        }

        importFlatten2(evaluator, packageName);
    }

    /**
     * Build and register the "flatten2" function.
     *
     * This requires the curry and flatten functions to already be registered.
     *
     * @param evaluator The evaluator to register flatten2 against.
     * @param packageName The name of the package.
     */
    public void importFlatten2(final Evaluator evaluator, final String packageName) {

        @SuppressWarnings("unchecked")
        final FunctionInterface<Object> flatten2 = (FunctionInterface<Object>)evaluator.evaluate(
                new Object[]{"curry", "callFlattened", new Object[]{"curry", "flatten"}}
        );

        if (packageName == null || packageName.isEmpty()) {
            evaluator.register("flatten2", flatten2);
        }
        else {
            evaluator.register(packageName + ".flatten2", flatten2);
        }
    }
}
