package com.github.basking2.sdsai.sexpr.functions;

import com.github.basking2.sdsai.sexpr.EvaluationContext;

import java.util.Iterator;
import java.util.function.BiFunction;

public interface FunctionInterface<R> extends BiFunction<Iterator<? extends Object>, EvaluationContext, R> {
}
