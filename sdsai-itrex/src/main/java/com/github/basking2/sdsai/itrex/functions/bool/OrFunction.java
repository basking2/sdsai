package com.github.basking2.sdsai.itrex.functions.bool;

/**
 */
public class OrFunction extends AbstractBooleanFunction {
    public OrFunction() {
        super(false);
    }

    @Override
    public Boolean booleanOperation(Boolean b1, Boolean b2) {
        return b1 || b2;
    }
}
