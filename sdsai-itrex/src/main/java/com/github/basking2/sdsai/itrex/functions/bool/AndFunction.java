package com.github.basking2.sdsai.itrex.functions.bool;

/**
 */
public class AndFunction extends AbstractBooleanFunction {

    public AndFunction() {
        super(true);
    }

    @Override
    public Boolean booleanOperation(Boolean b1, Boolean b2) {
        return b1 && b2;
    }
}
