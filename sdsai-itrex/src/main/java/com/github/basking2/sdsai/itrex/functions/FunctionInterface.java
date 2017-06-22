package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.EvaluationContext;

import java.util.Iterator;
import java.util.function.BiFunction;

public interface FunctionInterface<R> extends BiFunction<Iterator<?>, EvaluationContext, R> {
}
