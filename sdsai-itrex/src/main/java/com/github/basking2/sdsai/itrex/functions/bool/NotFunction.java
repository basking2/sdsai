package com.github.basking2.sdsai.itrex.functions.bool;

import com.github.basking2.sdsai.itrex.util.TwoTuple;

/**
 * Return the boolean value of the last argument to this function.
 */
public class NotFunction extends AbstractBooleanFunction {
    public NotFunction() {
        super(false);
    }

    @Override
    public TwoTuple<Boolean, Boolean> booleanOperation(Boolean b1, Boolean b2) {
        return new TwoTuple(Boolean.TRUE, !b2);
    }
}
