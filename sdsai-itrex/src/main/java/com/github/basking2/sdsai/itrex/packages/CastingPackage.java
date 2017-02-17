package com.github.basking2.sdsai.itrex.packages;

import com.github.basking2.sdsai.itrex.functions.CastingFunctionFactory;
import com.github.basking2.sdsai.itrex.functions.FunctionInterface;

/**
 * Using {@link CastingFunctionFactory} build type casting functions for import.
 */
public class CastingPackage {
    public static final FunctionInterface<String> toString = (itr, ctx) -> CastingFunctionFactory.castToString(itr.next());
    public static final FunctionInterface<Integer> toInt =  (itr, ctx) -> CastingFunctionFactory.castToInt(itr.next());
    public static final FunctionInterface<Float> toFloat = (itr, ctx) -> CastingFunctionFactory.castToFloat(itr.next());
    public static final FunctionInterface<Long> toLong =  (itr, ctx) -> CastingFunctionFactory.castToLong(itr.next());
    public static final FunctionInterface<Double> toDouble =  (itr, ctx) -> CastingFunctionFactory.castToDouble(itr.next());
}
