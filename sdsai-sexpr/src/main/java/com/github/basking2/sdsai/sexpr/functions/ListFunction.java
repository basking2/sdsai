package com.github.basking2.sdsai.sexpr.functions;

import com.github.basking2.sdsai.sexpr.EvaluationContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A function that materializes an iterator into a list and returns it.
 *
 * This is useful in situation where you want to set a variable using the "set" function
 * to return a list.
 *
 * Also, sometimes you want to defer evaluation of a list. In such situations you can make the first
 * element be the "list" function which simply returns the following arguments.
 */
public class ListFunction implements FunctionInterface<List<Object>> {
    @Override
    public List<Object> apply(Iterator<? extends Object> objectIterator, final EvaluationContext evaluationContext) {
        final List<Object> list = new ArrayList<>();

        while (objectIterator.hasNext()) {
            list.add(objectIterator.next());
        }

        return list;
    }
}
