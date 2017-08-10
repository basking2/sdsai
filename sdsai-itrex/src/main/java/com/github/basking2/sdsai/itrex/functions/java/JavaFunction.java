package com.github.basking2.sdsai.itrex.functions.java;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.SExprRuntimeException;
import com.github.basking2.sdsai.itrex.functions.AbstractFunction2;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static com.github.basking2.sdsai.itrex.util.Reflection.isAssignable;

/**
 * Function that takes the name of a function, a target object, and a list or iterable of arguments.
 *
 * If the argument is a collection or an iterator, each element is marshaled as an argument.
 * If the argument is neither an collection nor an iterator, it is marshaled as an argument itself.
 *
 * {@code
 * <pre>
 *     [java new [classOf java.util.ArrayList]]
 * </pre>
 * }
 *
 * {@code
 * <pre>
 *     [java add [get myArray] [list 1]]
 * </pre>
 * }
 *
 * {@code
 * <pre>
 *     [java add [get myArray] 1]
 * </pre>
 * }
 */
public class JavaFunction extends AbstractFunction2<String, Object, Object> {
    @Override
    protected Object applyImpl(final String functionName, final Object target, final Iterator<?> rest, final EvaluationContext context) {
        final List<Object> args = buildArgs(rest);

        try {
            return call(functionName, target, args);
        }
        catch (final Throwable t) {
            throw new SExprRuntimeException("Evaluating function "+functionName+ " on object "+target, t);
        }
    }

    private List<Object> buildArgs(final Iterator<?> rest) {
        final List<Object> args = new ArrayList<>();

        while (rest.hasNext()) {
            final Object o = rest.next();

            if (o instanceof Collection) {
                @SuppressWarnings("unchecked")
                final Collection<Object> collection = (Collection<Object>)o;
                args.addAll(collection);
            }

            else if (o instanceof Iterator) {
                for (
                        @SuppressWarnings("unchecked")
                        final Iterator<?> i = (Iterator<Object>)o;
                        i.hasNext();
                        )
                {
                    args.add(i.next());
                }
            }

            else {
                args.add(o);
            }
        }

        return args;
    }

    private Object call(final String functionName, final Object target, final List<?> args) throws Exception {
        final Method method = findMethod(
                target.getClass(),
                functionName,
                args.stream().map(Object::getClass).toArray(i->new Class<?>[i]));

        if (method == null) {
            throw new SExprRuntimeException("Method "+functionName+" not found for object "+target);
        }

        return method.invoke(target, args.toArray());
    }

    public static Method findMethod(final Class<?> clazz, final String name, final Class<?>[] argumentTypes) {
        for (final Method method : clazz.getMethods()) {
            nomatch: if (method.getName().equals(name)) {
                if (method.getParameterCount() == argumentTypes.length) {
                    final Class<?>[] parameterTypes = method.getParameterTypes();

                    for (int i = 0; i < parameterTypes.length; ++i) {
                        if (!isAssignable(parameterTypes[i], argumentTypes[i])) {
                            break nomatch;
                        }
                    }

                    return method;
                }

            }
        }

        return null;
    }
}
