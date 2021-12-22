/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.itrex.functions.java;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.SExprRuntimeException;
import com.github.basking2.sdsai.itrex.functions.AbstractFunction1;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static com.github.basking2.sdsai.itrex.util.Reflection.isAssignable;

public class JavaNewFunction extends AbstractFunction1<Object, Object> {
    @Override
    protected Object applyImpl(final Object target, final Iterator<?> rest, final EvaluationContext context) {
        final List<Object> args = buildArgs(rest);

        try {
            final Class<?> clazz;
            if (target instanceof String) {
                clazz = Class.forName((String)target);
            }
            else if (target instanceof Class) {
                clazz = (Class)target;
            }
            else {
                throw new SExprRuntimeException("First argument to javaNew must be a string or a class.");
            }

            return call(clazz, args);
        }
        catch (final Throwable t) {
            throw new SExprRuntimeException("Evaluating function constructor for "+target, t);
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

    private Object call(final Class<?> target, final List<?> args) throws Exception {
        final Constructor<?> constructor = findConstructor(target, args.stream().map(Object::getClass).toArray(i -> new Class<?>[i]));

        if (constructor == null) {
            throw new SExprRuntimeException("Constructor not found for object "+target);
        }

        return constructor.newInstance(args.toArray());
    }

    public static Constructor<?> findConstructor(final Class<?> clazz, final Class<?>[] argumentTypes) {
        for (final Constructor<?> constructor : clazz.getConstructors()) {

            nomatch: if (constructor.getParameterCount() == argumentTypes.length) {
                final Class<?>[] parameterTypes = constructor.getParameterTypes();

                for (int i = 0; i < parameterTypes.length; ++i) {
                    if (!isAssignable(parameterTypes[i], argumentTypes[i])) {
                        break nomatch;
                    }
                }

                return constructor;
            }
        }

        return null;
    }
}
