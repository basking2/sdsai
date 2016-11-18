package com.github.basking2.sdsai.sexpr.functions;

import com.github.basking2.sdsai.sexpr.EvaluationContext;

import java.util.Arrays;
import java.util.List;

public class StringSplitFunction extends AbstractFunction2<String,String, List<String>> {
    @Override
    protected List<String> applyImpl(final String pattern, final String str, final EvaluationContext context) {
        return Arrays.asList(str.split(pattern));
    }
}
