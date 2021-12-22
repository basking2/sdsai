/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.EvaluationContext;

import java.util.*;

/**
 * A function that materializes an iterator into a dictionary and returns it.
 *
 * This is useful for passing arguments into java {@code FunctionInterface}s you write and bind as functions.
 *
 * This is not termed, "Map", as in this language "map" is a function performed on sequences.
 *
 * If an odd number of arguments is given, the last one is set to NULL.
 */
public class DictFunction implements FunctionInterface<Map<Object, Object>> {
    @Override
    public Map<Object, Object> apply(Iterator<? extends Object> objectIterator, final EvaluationContext evaluationContext) {
        final Map<Object, Object> dict = new TreeMap<Object, Object>();

        while (objectIterator.hasNext()) {
            final Object key = objectIterator.next();
            final Object val;
            if (objectIterator.hasNext()) {
                val = objectIterator.next();
            }
            else {
                val = null;
            }

            dict.put(key, val);
        }

        return dict;
    }
}
