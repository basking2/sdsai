package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.Evaluator;
import com.github.basking2.sdsai.itrex.SExprRuntimeException;
import com.github.basking2.sdsai.itrex.packages.Package;

import java.lang.reflect.Field;
import java.util.Iterator;

/**
 * Import functions into the current runtime.
 *
 * Returns OK on success or an error message.
 */
public class ImportFunction implements FunctionInterface<String> {

    final Evaluator evaluator;
    final EvaluationContext evaluationContext;

    public ImportFunction(
            final Evaluator evaluator,
            final EvaluationContext evaluationContext
    ) {
        this.evaluator = evaluator;
        this.evaluationContext = evaluationContext;
    }

    @Override
    public String apply(final Iterator<?> iterator, final EvaluationContext evaluationContext) {

        final StringBuilder errors = new StringBuilder();

        final Object importMe = iterator.next();

        String destinationPackage = null;

        if (iterator.hasNext()) {
            final String keyWord = iterator.next().toString();
            if (! "as".equals(keyWord)) {
                throw new SExprRuntimeException("Expected \"as\" but got "+keyWord+" in import.");
            }

            if (iterator.hasNext()) {
                destinationPackage = iterator.next().toString();
            }
            else {
                destinationPackage = "";
            }
        }

        final String s = doImport(importMe, destinationPackage);
        if (s != null) {
            errors.append(s).append("\n");
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
     *
     * @return Null on success or an error string.
     */
    private String doImport(final Object o, final String destinationPackage) {
        if (o instanceof String) {
            try {
                final Class<?> clazz = getClass().forName((String)o);

                if (Package.class.isAssignableFrom(clazz)) {
                    try {
                        doImport(clazz.newInstance(), destinationPackage);
                    } catch (InstantiationException e) {
                        throw new SExprRuntimeException("Creating "+((String) o), e);
                    } catch (IllegalAccessException e) {
                        throw new SExprRuntimeException("Creating "+((String) o), e);
                    }
                }
                else {
                    doImportStatics(clazz, clazz, destinationPackage);
                }
            }
            catch (final ClassNotFoundException e) {
                return e.getMessage();
            }
        }
        else if (o instanceof Package) {
            final Package p = (Package)o;
            p.importTo(evaluator, destinationPackage);
        }
        else if (o instanceof Class) {
            return doImportStatics((Class)o, o, destinationPackage);
        }
        else {
            return doImportStatics(o.getClass(), o, destinationPackage);
        }

        return null;
    }

    private String doImportStatics(final Class<?> clazz, final Object target, final String destinationPackage) {
        String packageName = null;

        // If the user is not asking we import to a particular package, check for a given package name.
        if (destinationPackage == null) {
            for (final Field field: clazz.getFields()) {
                if (field.getName().equals("__package")) {
                    try {
                        packageName = field.get(target).toString();
                    } catch (IllegalAccessException e) {
                        return e.getMessage();
                    }
                }
            }
        }
        else {
            packageName = destinationPackage;
        }

        // Find a package field.
        for (final Field field : clazz.getFields()) {
            final Object func;
            try {
                func = field.get(target);
            } catch (IllegalAccessException e) {
                return e.getMessage();
            }
            if (func != null && func instanceof FunctionInterface) {
                if (packageName == null || packageName.isEmpty()) {
                    evaluationContext.register(field.getName(), (FunctionInterface<?>) func);
                }
                else {
                    evaluationContext.register(packageName + "." + field.getName(), (FunctionInterface<?>) func);
                }
            }
        }

        return null;
    }
}
