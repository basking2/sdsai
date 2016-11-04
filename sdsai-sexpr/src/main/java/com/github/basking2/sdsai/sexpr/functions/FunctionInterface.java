package com.github.basking2.sdsai.sexpr.functions;

import java.util.Iterator;
import java.util.function.Function;

public interface FunctionInterface<R> extends Function<Iterator<Object>, R> {
}
