package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.Evaluator;
import com.github.basking2.sdsai.itrex.util.TwoTuple;
import org.junit.Test;

import java.util.Iterator;

import static com.github.basking2.sdsai.itrex.SimpleExpressionParser.parseExpression;
import static org.junit.Assert.*;

public class ZipFunctionTest {
    @Test
    public void testZip() {
        final Evaluator e = new Evaluator();

        final Iterator<TwoTuple<Integer, Integer>> i = (Iterator<TwoTuple<Integer, Integer>>) e.evaluate(
                parseExpression(
                        "[zip padLeft [int 3] [list 1] [list 1 2]]"
                ));
        TwoTuple<Integer, Integer> tt;

        assertTrue(i.hasNext());
        tt = i.next();
        assertEquals(Integer.valueOf(1), tt.l);
        assertEquals(Integer.valueOf(1), tt.r);

        assertTrue(i.hasNext());
        tt = i.next();
        assertEquals(Integer.valueOf(3), tt.l);
        assertEquals(Integer.valueOf(2), tt.r);

        assertFalse(i.hasNext());
    }

    @Test
    public void testZip2() {
        final Evaluator e = new Evaluator();

        final Iterator<Integer> i = (Iterator<Integer>) e.evaluate(
                parseExpression(
                        "[map [fn head] [zip padLeft [int 3] [list 1] [list 1 2]]]"
                ));

        assertTrue(i.hasNext());
        assertEquals(Integer.valueOf(1), i.next());

        assertTrue(i.hasNext());
        assertEquals(Integer.valueOf(3), i.next());

        assertFalse(i.hasNext());
    }
}
