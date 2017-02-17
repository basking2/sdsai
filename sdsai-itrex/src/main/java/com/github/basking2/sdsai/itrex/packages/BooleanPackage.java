package com.github.basking2.sdsai.itrex.packages;

import com.github.basking2.sdsai.itrex.functions.FunctionInterface;
import com.github.basking2.sdsai.itrex.functions.bool.AndFunction;
import com.github.basking2.sdsai.itrex.functions.bool.CompareFunction;
import com.github.basking2.sdsai.itrex.functions.bool.NotFunction;
import com.github.basking2.sdsai.itrex.functions.bool.OrFunction;

/**
 * Common functions that return booleans. Comparisons and logical, such as and, or etc.
 */
public class BooleanPackage {
    public static final FunctionInterface<Boolean> and = new AndFunction();
    public static final FunctionInterface<Boolean> or = new OrFunction();
    public static final FunctionInterface<Boolean> not = new NotFunction();
    public static final FunctionInterface<Boolean> eq = new CompareFunction(CompareFunction.OP.EQ);
    public static final FunctionInterface<Boolean> gt = new CompareFunction(CompareFunction.OP.GT);
    public static final FunctionInterface<Boolean> gte = new CompareFunction(CompareFunction.OP.GTE);
    public static final FunctionInterface<Boolean> lt = new CompareFunction(CompareFunction.OP.LT);
    public static final FunctionInterface<Boolean> lte = new CompareFunction(CompareFunction.OP.LTE);

}
