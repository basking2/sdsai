package com.github.basking2.sdsai.itrex.packages;

import com.github.basking2.sdsai.itrex.functions.FunctionInterface;
import com.github.basking2.sdsai.itrex.functions.functional.*;

import java.util.Iterator;

/**
 * The contents of {@link com.github.basking2.sdsai.itrex.functions.functional}.
 */
public class FunctionalPackage {
    public static final FunctionInterface<FunctionInterface<Object>> curry = new CurryFunction();
    public static final FunctionInterface<FunctionInterface<Object>> compose = new ComposeFunction();
    public static final FunctionInterface<Iterator<Object>> map = new MapFunction();
    public static final FunctionInterface<Object> foldLeft = new FoldLeftFunction();
    public static final FunctionInterface<Object> fold = foldLeft;
    public static final FunctionInterface<Object> callFlattened = new CallFlattenedFunction();
}
