/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.itrex.functions.bool;

/**
 */
public class AndFunction extends AbstractBooleanFunction {

    public AndFunction() {
        super(true);
    }

    @Override
    public Result booleanOperation(Boolean b1, Boolean b2) {
        final boolean b = b1 && b2;
        if (b) {
            // If b is true, it might go false. Continue.
            return new Result(true, b);
        }
        else {
            return new Result(false, b);
        }
    }
}
