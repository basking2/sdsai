/**
 * Copyright (c) 2023 Sam Baskinger
 */
package com.github.basking2.sdsai.itrex.iterators;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParallelMappingIteratorTest {
    @Test
    public void testMapping() {
        final ExecutorService es = Executors.newFixedThreadPool(10);
        final Iterator<Integer> itr = new ParallelMappingIterator<Integer, Integer>(
                false,
                new RangeIterator(0,10, 1),
                es,
                10,
                (i) -> {
                    Thread.sleep((int)(Math.random()*100));
                    return i+1;
                }
                );

        // NOTE - we bump the range up from 0-10 to 1-11 to accommodate the mapping function above.
        final Integer[] expectedList = Iterators.toList(
            new RangeIterator(1,11, 1)).toArray(new Integer[0]);

        final List<Integer> actualList = Iterators.toList(itr);

        for (final Integer e : expectedList) {
            assertTrue(actualList.contains(e));
        }
    }
}
