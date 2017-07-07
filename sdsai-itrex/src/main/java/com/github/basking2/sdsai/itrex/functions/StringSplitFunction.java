package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.EvaluationContext;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class StringSplitFunction extends AbstractFunction2<String,String, List<String>> {
    @Override
    protected List<String> applyImpl(final String pattern, final String str, final Iterator<?> rest, final EvaluationContext context) {
        return Arrays.asList(str.split(pattern));
    }
}
