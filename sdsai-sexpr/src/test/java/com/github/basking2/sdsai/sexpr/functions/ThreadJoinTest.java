package com.github.basking2.sdsai.sexpr.functions;

import com.github.basking2.sdsai.sexpr.Evaluator;
import org.junit.Test;

import java.util.Iterator;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

/**
 * Test {@link ThreadFunction} and {@link JoinFunction}.
 */
public class ThreadJoinTest {

    @Test
    public void testThreadJoin() {
        final Evaluator evaluator = new Evaluator();

        final Object expression = asList("join", asList("thread", asList("list", 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)));

        final Iterator<Integer> results = (Iterator<Integer>) evaluator.evaluate(expression);

        assertEquals(Integer.valueOf(1), results.next());
        assertEquals(Integer.valueOf(2), results.next());
        assertEquals(Integer.valueOf(3), results.next());
        assertEquals(Integer.valueOf(4), results.next());
        assertEquals(Integer.valueOf(5), results.next());
        assertEquals(Integer.valueOf(6), results.next());
        assertEquals(Integer.valueOf(7), results.next());
        assertEquals(Integer.valueOf(8), results.next());
        assertEquals(Integer.valueOf(9), results.next());
        assertEquals(Integer.valueOf(10), results.next());
        assertFalse(results.hasNext());

    }
}
