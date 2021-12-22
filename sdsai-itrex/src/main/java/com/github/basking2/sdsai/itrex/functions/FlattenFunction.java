/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.iterators.IteratorIterator;

import java.util.Arrays;
import java.util.Iterator;

import static com.github.basking2.sdsai.itrex.iterators.Iterators.mapIterator;

/**
 * Flatten an iterator of iterators a single layer.
 *
 * That is, if you have an iterator-of-iterator-of-iterators, you will get back an iterator-of-iterators.
 * If you have an iterator-of-iterator-of-ints, you will get back an iterator of ints.
 *
 * Another way to view this is that it takes away the top-level iterator.
 */
public class FlattenFunction implements FunctionInterface<Iterator<Object>> {
    @SuppressWarnings("unchecked")
    @Override
    public Iterator<Object> apply(final Iterator<?> iterator, final EvaluationContext evaluationContext) {

        Iterator<Iterator<Object>> itrItrObj = mapIterator(iterator, e -> {
            if (e instanceof Iterator) {
                return (Iterator<Object>) e;
            }

            if (e instanceof Iterable) {
                return ((Iterable<Object>)e).iterator();
            }

            if (e instanceof Object[]) {
                return Arrays.asList((Object[])e).iterator();
            }

            return Arrays.asList((Object)e).iterator();

        });

        return new IteratorIterator<>(itrItrObj);
    }
}
