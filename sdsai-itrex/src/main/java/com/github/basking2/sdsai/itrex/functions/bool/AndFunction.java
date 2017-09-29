package com.github.basking2.sdsai.itrex.functions.bool;

import com.github.basking2.sdsai.itrex.util.TwoTuple;

/**
 */
public class AndFunction extends AbstractBooleanFunction {

    public AndFunction() {
        super(true);
    }

    @Override
    public TwoTuple<Boolean, Boolean> booleanOperation(Boolean b1, Boolean b2) {
        final boolean b = b1 && b2;
        if (b) {
            // If b is true, it might go false. Continue.
            return new TwoTuple<>(Boolean.TRUE, b);
        }
        else {
            return new TwoTuple<>(Boolean.FALSE, b);
        }
    }
}
