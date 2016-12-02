package com.github.basking2.sdsai.itrex.functions.bool;

/**
 * Return the boolean value of the last argument to this function.
 */
public class NotFunction extends AbstractBooleanFunction {
    public NotFunction() {
        super(false);
    }

    @Override
    public Boolean booleanOperation(Boolean b1, Boolean b2) {
        return !b2;
    }
}
