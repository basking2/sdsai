/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.itrex.packages;

import com.github.basking2.sdsai.itrex.Evaluator;
import com.github.basking2.sdsai.itrex.functions.FunctionInterface;

/**
 * An interface for more explicitly package importing.
 *
 * Most Itrex packages are just classes with static {@link FunctionInterface} fields set.
 */
public interface Package {
    /**
     * Import some set of functions into the given {@link com.github.basking2.sdsai.itrex.Evaluator}.
     *
     * @param evaluator The evaluator to import to.
     * @param packageName If not null, all functions should be registered under this package name,
     *                    per the user's import request. That is, function names should be
     *                    constructed as {@code packageName +"."+functionName}.
     */
    void importTo(final Evaluator evaluator, final String packageName);
}
