/**
 * Copyright (c) 2023 Sam Baskinger
 */
package com.github.basking2.sdsai.itrex.iterators;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

public class BufferIteratorTest {

    @Test
    public void testEmpty() {
        assertThrows(NoSuchElementException.class, () -> {

            final BufferIterator bi = new BufferIterator(10, asList("a", "b", "c").iterator());

            bi.next();
            bi.next();
            bi.next();
            bi.next();
        });
    }

    @Test
    public void testBufferFill() {
        final BufferIterator bi = new BufferIterator(10, asList("a", "b", "c").iterator());

        assertTrue(bi.hasNext());
        assertEquals("a", bi.next());
        assertTrue(bi.hasNext());
        assertEquals("b", bi.next());
        assertTrue(bi.hasNext());
        assertEquals("c", bi.next());
        assertFalse(bi.hasNext());
    }

    @Test
    public void testIterationLargerThanBufferSize() {

        final List<Integer> source = new ArrayList<>();

        for (int i = 0; i < 132; i++) {
            source.add((int)(Math.random() * Integer.MAX_VALUE));
        }

        assertTrue(source.size() > 0);

        final BufferIterator bi = new BufferIterator(10, source.iterator());

        for (int i = 0; i < source.size(); i++) {
            assertTrue(bi.hasNext());
            assertEquals(source.get(i), bi.next());
        }
    }
}
