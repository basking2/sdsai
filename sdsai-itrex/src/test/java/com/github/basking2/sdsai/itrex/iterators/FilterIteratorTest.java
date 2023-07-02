/**
 * Copyright (c) 2023 Sam Baskinger
 */
package com.github.basking2.sdsai.itrex.iterators;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class FilterIteratorTest {

    @Test
    public void testError() {
        assertThrows(NoSuchElementException.class, () -> {

            final FilterIterator<Integer> f = new FilterIterator<Integer>(Arrays.asList(1, 2, 3).iterator(), i -> i.intValue() != 2);

            assertTrue(f.hasNext());
            assertEquals(Integer.valueOf(1), f.next());

            assertTrue(f.hasNext());
            assertEquals(Integer.valueOf(3), f.next());

            assertFalse(f.hasNext());
            f.next();
        });
    }

    @Test
    public void testNullsOk() {
        final FilterIterator<Integer> f = new FilterIterator<Integer>(Arrays.asList(1,null,3).iterator(), i -> true);

        assertTrue(f.hasNext());
        assertEquals(Integer.valueOf(1), f.next());

        assertTrue(f.hasNext());
        assertEquals((Integer)null, f.next());

        assertTrue(f.hasNext());
        assertEquals(Integer.valueOf(3), f.next());

        assertFalse(f.hasNext());
    }
}
