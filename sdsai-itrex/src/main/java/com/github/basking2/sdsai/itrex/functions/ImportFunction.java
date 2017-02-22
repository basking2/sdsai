package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.Evaluator;

import java.lang.reflect.Field;
import java.util.Iterator;

/**
 * Import functions into the current runtime.
 *
 * Returns OK on success or an error message.
 */
public class ImportFunction implements FunctionInterface<String> {

    final Evaluator evaluator;

    public ImportFunction(final Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public String apply(final Iterator<?> iterator, final EvaluationContext evaluationContext) {

        final StringBuilder errors = new StringBuilder();

        while (iterator.hasNext()) {
            final String s = doImport(iterator.next());
            if (s != null) {
                errors.append(s).append("\n");
            }
        }

        if (errors.length() > 0) {
            return errors.toString();
        }
        else {
            return "OK";
        }
    }

    /**
     * @param o The object to import.
     * @return Null on success or an error string.
     */
    private String doImport(final Object o) {
        if (o instanceof String) {
            try {
                final Class<?> clazz = getClass().forName((String)o);
                doImportStatics(clazz, clazz);
            }
            catch (final ClassNotFoundException e) {
                return e.getMessage();
            }
        }
        else if (o instanceof Class) {
            return doImportStatics((Class)o, o);
        }
        else {
            return doImportStatics(o.getClass(), o);
        }

        return null;
    }

    private String doImportStatics(final Class<?> clazz, final Object target) {
        for (final Field field : clazz.getFields()) {
            final Object func;
            try {
                func = field.get(target);
            } catch (IllegalAccessException e) {
                return e.getMessage();
            }
            if (func != null && func instanceof FunctionInterface) {
                evaluator.register(field.getName(), (FunctionInterface<?>) func);
            }
        }

        return null;
    }
}