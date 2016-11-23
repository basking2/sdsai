package com.github.basking2.sdsai.itrex;

import com.github.basking2.sdsai.itrex.functions.AbstractFunction1;
import com.github.basking2.sdsai.itrex.functions.AbstractFunction2;
import com.github.basking2.sdsai.itrex.functions.FunctionInterface;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * An {@link Evaluator} that binds {@link Math} functions.
 */
public class JavaMathEvaluator extends Evaluator {

    public JavaMathEvaluator() {
        super();
        register("toInt", (i, ctx) -> castToInt(i.next()));
        register("toFloat", (i, ctx) -> castToFloat(i.next()));
        register("toDouble", (i, ctx) -> castToDouble(i.next()));
        register("toLong", (i, ctx) -> castToLong(i.next()));
    }

    @Override
    public FunctionInterface<? extends Object> getFunction(final Object object) {
        final String functionName = object.toString();

        for (final Method m : Math.class.getMethods()) {
            if (functionName.equalsIgnoreCase(m.getName())) {
                if (m.getParameterCount() == 2) {
                    return new MathFunction2(functionName);
                }
                if (m.getParameterCount() == 1) {
                    return new MathFunction1(functionName);
                }
            }
        }

        return super.getFunction(object);
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
                o2 = castToDouble(o2);
            } else if (o2 instanceof Double) {
                o1 = castToDouble(o1);
            } else if (!o1.getClass().isInstance(o2.getClass())) {
                o1 = castToDouble(o1);
                o2 = castToDouble(o2);
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
    private static Float castToFloat(Object o) {
        if (o instanceof Double) {
            return ((Double)o).floatValue();
        }
        else if (o instanceof Float) {
            return ((Float)o);
        }
        else if (o instanceof Long) {
            return ((Long)o).floatValue();
        }
        else if (o instanceof Integer) {
            return ((Integer)o).floatValue();
        }
        else {
            throw new IllegalArgumentException("Cannot cast "+o.getClass().getName()+" to double.");
        }
    }
    private static Integer castToInt(Object o) {
        if (o instanceof Double) {
            return ((Double)o).intValue();
        }
        else if (o instanceof Float) {
            return ((Float)o).intValue();
        }
        else if (o instanceof Long) {
            return ((Long)o).intValue();
        }
        else if (o instanceof Integer) {
            return ((Integer)o);
        }
        else {
            throw new IllegalArgumentException("Cannot cast "+o.getClass().getName()+" to double.");
        }
    }

    private static Long castToLong(Object o) {
        if (o instanceof Double) {
            return ((Double)o).longValue();
        }
        else if (o instanceof Float) {
            return ((Float)o).longValue();
        }
        else if (o instanceof Long) {
            return ((Long)o);
        }
        else if (o instanceof Integer) {
            return ((Integer)o).longValue();
        }
        else {
            throw new IllegalArgumentException("Cannot cast "+o.getClass().getName()+" to double.");
        }
    }

    private static Double castToDouble(final Object o) {
        if (o instanceof Double) {
            return (Double)o;
        }
        else if (o instanceof Float) {
            return ((Float)o).doubleValue();
        }
        else if (o instanceof Long) {
            return ((Long)o).doubleValue();
        }
        else if (o instanceof Integer) {
            return ((Integer)o).doubleValue();
        }
        else {
            throw new IllegalArgumentException("Cannot cast "+o.getClass().getName()+" to double.");
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
