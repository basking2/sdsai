/**
 * Copyright (c) 2023 Sam Baskinger
 */
package com.github.basking2.sdsai.itrex.iterators;

import com.github.basking2.sdsai.itrex.util.TwoTuple;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

public class ZipIteratorTest {
    @Test
    public void testNoPadding() {
        final Iterator<TwoTuple<Integer, Integer>> i = new ZipIterator<>(
                new RangeIterator(0, 3, 1),
                new RangeIterator(0, 30, 1)
        );

        assertTrue(i.hasNext());
        assertEquals(new TwoTuple<>(0, 0), i.next());
        assertTrue(i.hasNext());
        assertEquals(new TwoTuple<>(1, 1), i.next());
        assertTrue(i.hasNext());
        assertEquals(new TwoTuple<>(2, 2), i.next());
        assertFalse(i.hasNext());
    }

    @Test
    public void testRightPadding() {
        final Iterator<TwoTuple<Integer, Integer>> i = new ZipIterator<>(
                new RangeIterator(0, 3, 1),
                true,
                -1,
                new RangeIterator(0, 4, 1),
                true,
                -1
        );

        assertTrue(i.hasNext());
        assertEquals(new TwoTuple<>(0, 0), i.next());
        assertTrue(i.hasNext());
        assertEquals(new TwoTuple<>(1, 1), i.next());
        assertTrue(i.hasNext());
        assertEquals(new TwoTuple<>(2, 2), i.next());
        assertTrue(i.hasNext());
        assertEquals(new TwoTuple<>(-1, 3), i.next());
        assertFalse(i.hasNext());
    }

    @Test
    public void testLeftPadding() {
        final Iterator<TwoTuple<Integer, Integer>> i = new ZipIterator<>(
                new RangeIterator(0, 4, 1),
                true,
                -1,
                new RangeIterator(0, 3, 1),
                true,
                -1
        );

        assertTrue(i.hasNext());
        assertEquals(new TwoTuple<>(0, 0), i.next());
        assertTrue(i.hasNext());
        assertEquals(new TwoTuple<>(1, 1), i.next());
        assertTrue(i.hasNext());
        assertEquals(new TwoTuple<>(2, 2), i.next());
        assertTrue(i.hasNext());
        assertEquals(new TwoTuple<>(3, -1), i.next());
        assertFalse(i.hasNext());
    }
}
