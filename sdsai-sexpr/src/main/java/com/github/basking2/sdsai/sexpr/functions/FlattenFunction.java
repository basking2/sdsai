package com.github.basking2.sdsai.sexpr.functions;

import com.github.basking2.sdsai.sexpr.util.IteratorIterator;

import java.util.Iterator;

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
    public Iterator<Object> apply(Iterator<?> iterator) {

        // NOTE - the type-cast is critical to get the correct constructor.
        return new IteratorIterator<Object>((Iterator<Iterator<?>>)iterator);
    }
}
