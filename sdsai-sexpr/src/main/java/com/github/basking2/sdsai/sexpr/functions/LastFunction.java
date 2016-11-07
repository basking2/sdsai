package com.github.basking2.sdsai.sexpr.functions;

import java.util.Iterator;

/**
 * Evaluate all arguments, but only return the value of the last element.
 */
public class LastFunction implements FunctionInterface<Object> {
    
    @Override
    public Object apply(final Iterator<? extends Object> objectIterator) {
        Object o = null;

        while (objectIterator.hasNext()) {
            o = objectIterator.next();
        }

        return o;
    }

}
