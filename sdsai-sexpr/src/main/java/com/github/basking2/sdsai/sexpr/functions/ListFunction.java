package com.github.basking2.sdsai.sexpr.functions;

import java.util.Iterator;

/**
 * A function that forwards a list without any evaluation.
 *
 * Sometimes you want to defer evaluation of a list. In such sitations you can make the first
 * element be the "list" function which simply returns the following arguments.
 */
public class ListFunction implements FunctionInterface<Iterator<Object>> {
    @Override
    public Iterator<Object> apply(Iterator<Object> objectIterator) {
        return objectIterator;
    }
}
