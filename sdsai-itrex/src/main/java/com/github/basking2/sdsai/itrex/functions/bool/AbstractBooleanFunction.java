package com.github.basking2.sdsai.itrex.functions.bool;

import com.github.basking2.sdsai.itrex.functions.AbstractAggregatingFunction;
import com.github.basking2.sdsai.itrex.util.TypeConversion;

/**
 * Perform a boolean function on all arguments.
 */
public abstract class AbstractBooleanFunction extends AbstractAggregatingFunction<Object, Boolean> {

    /**
     * Construct an function that will start aggregating values with initialValue as the first R value.
     * <p>
     * If an implementation is summing numbers, initialValue may be 0.
     * If an implementation is building a list, initialValue may be an empty list.
     *
     * @param initialValue An initial value.
     */
    public AbstractBooleanFunction(Boolean initialValue) {
        super(initialValue);
    }

    @Override
    public Boolean applyT(Boolean aBoolean, Object o) {
        return booleanOperation(aBoolean, TypeConversion.toBoolean(o));
    }

    public abstract Boolean booleanOperation(Boolean b1, Boolean b2);
}

