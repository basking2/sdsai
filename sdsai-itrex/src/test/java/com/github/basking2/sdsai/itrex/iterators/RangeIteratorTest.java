/**
 * Copyright (c) 2023 Sam Baskinger
 */
package com.github.basking2.sdsai.itrex.iterators;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RangeIteratorTest {
    @Test
    public void testInvalidRangeZeroStep() {
        assertThrows(IllegalArgumentException.class, () -> {
            new RangeIterator(0, 0, 0);
        });
    }

    @Test
    public void testRangeUp() {
        assertThrows(NoSuchElementException.class, () -> {

            final Iterator<Integer> i = new RangeIterator(0, 3, 2);

            assertEquals(Integer.valueOf(0), i.next());
            assertEquals(Integer.valueOf(2), i.next());
            i.next();
        });
    }

    @Test
    public void testRangeDown() {
        assertThrows(NoSuchElementException.class, () -> {
            final Iterator<Integer> i = new RangeIterator(3, -2, -2);

            assertEquals(Integer.valueOf(3), i.next());
            assertEquals(Integer.valueOf(1), i.next());
            assertEquals(Integer.valueOf(-1), i.next());
            i.next();
        });
    }
}
