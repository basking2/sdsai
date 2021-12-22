/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.itrex.packages;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.Evaluator;
import com.github.basking2.sdsai.itrex.SExprRuntimeException;
import com.github.basking2.sdsai.itrex.functions.AbstractFunction1;
import com.github.basking2.sdsai.itrex.functions.AbstractFunction2;
import com.github.basking2.sdsai.itrex.functions.FunctionInterface;
import com.github.basking2.sdsai.itrex.util.TypeConversion;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

/**
 */
public class JavaMathPackage implements Package {
    @Override
    public void importTo(final Evaluator evaluator, String packageName) {

        for (final Method m : Math.class.getMethods()) {

            final String functionName;
            if (packageName == null) {
                functionName = "javamath." + m.getName();
            }
            else if (packageName.isEmpty()) {
                functionName = m.getName();
            }
            else {
                functionName = packageName + "." + m.getName();
            }

            if (m.getParameterCount() == 2) {
                FunctionInterface<? extends Object> f = new MathFunction2(m.getName());
                evaluator.register(functionName, f);
            }
            else if (m.getParameterCount() == 1) {
                FunctionInterface<? extends Object> f = new MathFunction1(m.getName());
                evaluator.register(functionName, f);
            }
        }
    }

    private static class MathFunction1 extends AbstractFunction1<Object, Object> {
        private final String functionName;

        public MathFunction1(final String functionName) {
            this.functionName = functionName;
        }

        @Override
        protected Object applyImpl(Object o, Iterator<?> rest, EvaluationContext context) {
            try {
                final Method m = Math.class.getMethod(functionName, getPrimitiveType(o));
                return m.invoke(null, o);
            }
            catch (final NoSuchMethodException e) {
                throw new SExprRuntimeException(e.getMessage(), e);
            }
            catch (final InvocationTargetException e) {
                throw new SExprRuntimeException(e.getMessage(), e);
            }
            catch (final IllegalAccessException e) {
                throw new SExprRuntimeException(e.getMessage(), e);
            }
        }
    }

    private static class MathFunction2 extends AbstractFunction2<Object, Object, Object> {

        private final String functionName;

        public MathFunction2(final String functionName) {
            this.functionName = functionName;
        }

        @Override
        protected Object applyImpl(Object o1, Object o2, Iterator<?> rest, EvaluationContext context) {
            if (o1 instanceof Double) {
                o2 = TypeConversion.toDouble(o2);
            } else if (o2 instanceof Double) {
                o1 = TypeConversion.toDouble(o1);
            } else if (!o1.getClass().isInstance(o2.getClass())) {
                o1 = TypeConversion.toDouble(o1);
                o2 = TypeConversion.toDouble(o2);
            }

            final Method m;
            try {
                m = Math.class.getMethod(functionName, getPrimitiveType(o1), getPrimitiveType(o2));
                return m.invoke(null, o1, o2);
            }
            catch (final NoSuchMethodException e) {
                throw new SExprRuntimeException("Method named " + functionName + " with arguments of type " + o1.getClass().getName() + " and " + o2.getClass().getName() + " not found.", e);
            }
            catch (final IllegalAccessException e) {
                throw new SExprRuntimeException(e.getMessage(), e);
            }
            catch (final InvocationTargetException e) {
                throw new SExprRuntimeException(e.getMessage(), e);
            }
        }
    }

    private static Class<?> getPrimitiveType(final Object o) {
        if (o instanceof Double) {
            return Double.TYPE;
        }
        else if (o instanceof Float) {
            return Float.TYPE;
        }
        else if (o instanceof Long) {
            return Long.TYPE;
        }
        else if (o instanceof Integer) {
            return Integer.TYPE;
        }
        else {
            return o.getClass();
        }
    }
}
