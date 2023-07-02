/**
 * Copyright (c) 2023 Sam Baskinger
 */
package com.github.basking2.sdsai.itrex.iterators;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PagingIteratorTest {
    @Test
    public void testPaging() {
        final Iterator<Integer> i = new RangeIterator(0, 10, 1);
        final Iterator<Iterator<Integer>> pi = new PagingIterator<>(1, i);
        final List<Integer> results = new ArrayList<Integer>();

        while (pi.hasNext()) {
            final Iterator<Integer> subIterator = pi.next();
            assertTrue(subIterator.hasNext());
            results.add(subIterator.next());
            assertFalse(subIterator.hasNext());
        }

        assertFalse(i.hasNext());
        assertFalse(pi.hasNext());

        final Integer[] expectedList = Iterators.toList(
                new RangeIterator(0,10, 1)).toArray(new Integer[0]);

        for (final Integer e : expectedList) {
            assertTrue(results.contains(e));
        }
    }
}
