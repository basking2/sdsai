package com.github.basking2.sdsai.itrex.iterators;

import org.junit.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;

public class RangeIteratorTest {
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidRangeZeroStep() {
        new RangeIterator(0, 0, 0);
    }

    @Test(expected = NoSuchElementException.class)
    public void testRangeUp() {
        final Iterator<Integer> i = new RangeIterator(0, 3, 2);

        assertEquals(Integer.valueOf(0), i.next());
        assertEquals(Integer.valueOf(2), i.next());
        i.next();
    }

    @Test(expected = NoSuchElementException.class)
    public void testRangeDown() {
        final Iterator<Integer> i = new RangeIterator(3, -2, -2);

        assertEquals(Integer.valueOf(3), i.next());
        assertEquals(Integer.valueOf(1), i.next());
        assertEquals(Integer.valueOf(-1), i.next());
        i.next();
    }
}
