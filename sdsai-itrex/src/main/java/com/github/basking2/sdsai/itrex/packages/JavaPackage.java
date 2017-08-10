package com.github.basking2.sdsai.itrex.packages;

import com.github.basking2.sdsai.itrex.functions.FunctionInterface;
import com.github.basking2.sdsai.itrex.functions.java.ClassOfFunction;
import com.github.basking2.sdsai.itrex.functions.java.JavaFunction;
import com.github.basking2.sdsai.itrex.functions.java.JavaNewFunction;

public class JavaPackage {
    public static final FunctionInterface<Object> java = new JavaFunction();
    public static final FunctionInterface<Object> javaNew = new JavaNewFunction();
    public static final FunctionInterface<Class<?>> classOf = new ClassOfFunction();
}
