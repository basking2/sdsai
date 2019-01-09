package com.github.basking2.sdsai.itrex.iterators;

import org.junit.Test;

import java.util.NoSuchElementException;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class BufferIteratorTest {

    @Test(expected = NoSuchElementException.class)
    public void testEmpty() {
        final BufferIterator bi = new BufferIterator(10, asList("a", "b", "c").iterator());

        bi.next();
        bi.next();
        bi.next();
        bi.next();
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
}
