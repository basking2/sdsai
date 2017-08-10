package com.github.basking2.sdsai.itrex.functions.java;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.SExprRuntimeException;
import com.github.basking2.sdsai.itrex.functions.AbstractFunction1;

import java.util.Iterator;

public class ClassOfFunction extends AbstractFunction1<String, Class<?>> {

    @Override
    protected Class<?> applyImpl(final String className, final Iterator<?> rest, final EvaluationContext context) {
        try {
            return Class.forName(className);
        }
        catch (final ClassNotFoundException e) {
            throw new SExprRuntimeException("Loading class "+className, e);
        }
    }
}
