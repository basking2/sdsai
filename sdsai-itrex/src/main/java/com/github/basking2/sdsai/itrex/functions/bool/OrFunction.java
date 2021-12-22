/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.itrex.functions.bool;

/**
 */
public class OrFunction extends AbstractBooleanFunction {

    public OrFunction() {
        super(false);
    }

    @Override
    public Result booleanOperation(Boolean b1, Boolean b2) {
        final boolean b = b1 || b2;
        if (b) {
            return new Result(false, b);
        }
        else {
            // b is false, so it could flip to true. Continue.
            return new Result(true, b);
        }
    }
}
