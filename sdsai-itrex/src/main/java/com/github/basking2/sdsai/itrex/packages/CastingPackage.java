package com.github.basking2.sdsai.itrex.packages;

import com.github.basking2.sdsai.itrex.functions.FunctionInterface;
import com.github.basking2.sdsai.itrex.util.TypeConversion;

/**
 * Using {@link TypeConversion} build type casting functions for import.
 */
public class CastingPackage {
    public static final FunctionInterface<String> toString = (itr, ctx) -> TypeConversion.toString(itr.next());
    public static final FunctionInterface<Integer> toInt =  (itr, ctx) -> TypeConversion.toInt(itr.next());
    public static final FunctionInterface<Float> toFloat = (itr, ctx) -> TypeConversion.toFloat(itr.next());
    public static final FunctionInterface<Long> toLong =  (itr, ctx) -> TypeConversion.toLong(itr.next());
    public static final FunctionInterface<Double> toDouble =  (itr, ctx) -> TypeConversion.toDouble(itr.next());
    public static final FunctionInterface<Boolean> toBoolean =  (itr, ctx) -> TypeConversion.toBoolean(itr.next());
}
