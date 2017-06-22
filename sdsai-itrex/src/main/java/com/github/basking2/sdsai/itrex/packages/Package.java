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
     */
    void importTo(final Evaluator evaluator);
}
