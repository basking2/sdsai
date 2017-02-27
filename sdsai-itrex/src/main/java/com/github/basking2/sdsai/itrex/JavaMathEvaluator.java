package com.github.basking2.sdsai.itrex;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.github.basking2.sdsai.itrex.functions.AbstractFunction1;
import com.github.basking2.sdsai.itrex.functions.AbstractFunction2;
import com.github.basking2.sdsai.itrex.functions.CastingFunctionFactory;
import com.github.basking2.sdsai.itrex.functions.FunctionInterface;

/**
 * An {@link Evaluator} that binds {@link Math} functions.
 */
public class JavaMathEvaluator extends Evaluator {

    public JavaMathEvaluator() {
        importMath();
    }

    public void importMath() {

        for (final Method m : Math.class.getMethods()) {

            final String functionName = m.getName();

            if (m.getParameterCount() == 2) {
                FunctionInterface<? extends Object> f = new MathFunction2(functionName);
                register(functionName, f);
            }
            else if (m.getParameterCount() == 1) {
                FunctionInterface<? extends Object> f = new MathFunction1(functionName);
                register(functionName, f);
            }
        }
    }

    private static class MathFunction1 extends AbstractFunction1<Object, Object> {
        private final String functionName;

        public MathFunction1(final String functionName) {
            this.functionName = functionName;
        }

        @Override
        protected Object applyImpl(Object o, EvaluationContext context) {
            try {
                final Method m = Math.class.getMethod(functionName, getPrimitiveType(o));
                return m.invoke(null, o);
            }
            catch (NoSuchMethodException e) {
                throw new SExprRuntimeException(e.getMessage(), e);
            }
            catch (InvocationTargetException e) {
                throw new SExprRuntimeException(e.getMessage(), e);
            }
            catch (IllegalAccessException e) {
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
        protected Object applyImpl(Object o1, Object o2, EvaluationContext context) {
            if (o1 instanceof Double) {
                o2 = CastingFunctionFactory.castToDouble(o2);
            } else if (o2 instanceof Double) {
                o1 = CastingFunctionFactory.castToDouble(o1);
            } else if (!o1.getClass().isInstance(o2.getClass())) {
                o1 = CastingFunctionFactory.castToDouble(o1);
                o2 = CastingFunctionFactory.castToDouble(o2);
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
