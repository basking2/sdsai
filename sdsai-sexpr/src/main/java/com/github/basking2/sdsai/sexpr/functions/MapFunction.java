package com.github.basking2.sdsai.sexpr.functions;

import com.github.basking2.sdsai.sexpr.SExprRuntimeException;
import com.github.basking2.sdsai.sexpr.util.Iterators;
import com.github.basking2.sdsai.sexpr.util.MappingIterator;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Map a function across all subsequent arguments.
 */
public class MapFunction implements FunctionInterface<Iterator<Object>> {
    @Override
    public Iterator<Object> apply(final Iterator<Object> objectIterator) {

        // If no function, then no results. No worries.
        if (!objectIterator.hasNext()) {
            return new ArrayList<Object>().iterator();
        }

        final Object functionObject = objectIterator.next();
        if (!(functionObject instanceof FunctionInterface)) {
            throw new SExprRuntimeException("First argument to map must be a function.");
        }

        final FunctionInterface<Object> function = (FunctionInterface<Object>)(functionObject);

        return new MappingIterator<Object, Object>(objectIterator, o -> function.apply(Iterators.wrap(o)));
    }
}
