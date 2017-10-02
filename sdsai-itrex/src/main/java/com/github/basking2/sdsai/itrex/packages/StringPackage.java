package com.github.basking2.sdsai.itrex.packages;

import com.github.basking2.sdsai.itrex.functions.FunctionInterface;
import com.github.basking2.sdsai.itrex.functions.StringConcatFunction;
import com.github.basking2.sdsai.itrex.functions.StringJoinFunction;
import com.github.basking2.sdsai.itrex.functions.StringSplitFunction;

import java.util.List;

/**
 * The string package of functions.
 *
 * Evaluate: [import com.github.basking2.sdsai.itrex.packages.StringPackage]
 */
public class StringPackage {
    public static final String __package = "string";

    public static final FunctionInterface<String> join = new StringJoinFunction();
    public static final FunctionInterface<String> concat = new StringConcatFunction();
    public static final FunctionInterface<List<String>> split = new StringSplitFunction();
}
