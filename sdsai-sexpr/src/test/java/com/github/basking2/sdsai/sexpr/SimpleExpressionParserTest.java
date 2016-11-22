package com.github.basking2.sdsai.sexpr;

import org.junit.Test;

import java.util.List;

import static com.github.basking2.sdsai.sexpr.SimpleExpressionParser.parseExpression;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class SimpleExpressionParserTest {
    @Test
    public void testEmptyList() {
        final List<Object> l = (List<Object>)parseExpression("[]");
    }

    @Test
    public void testLongList() {
        final List<Object> l = (List<Object>)parseExpression("[1  2l  3L]");
        assertEquals(3, l.size());
        assertEquals(Long.valueOf(1), l.get(0));
        assertEquals(Long.valueOf(2), l.get(1));
        assertEquals(Long.valueOf(3), l.get(2));
    }

    @Test
    public void testLongListList() {
        final List<List<Object>> l = (List<List<Object>>)parseExpression("[[1]  [2l  3L]]");
        assertEquals(2, l.size());
        assertEquals(Long.valueOf(1), l.get(0).get(0));
        assertEquals(Long.valueOf(2), l.get(1).get(0));
        assertEquals(Long.valueOf(3), l.get(1).get(1));
    }

    @Test
    public void testDoubleListList() {
        final List<List<Object>> l = (List<List<Object>>)parseExpression("[[1.0]  [2d  3D]]");
        assertEquals(2, l.size());
        assertEquals(Double.valueOf(1), l.get(0).get(0));
        assertEquals(Double.valueOf(2), l.get(1).get(0));
        assertEquals(Double.valueOf(3), l.get(1).get(1));
    }

    @Test
    public void testWords() {
        final List<String> l = (List<String>)parseExpression("[hi bye hello \"how are you?\"]");
        assertEquals("hi", l.get(0));
        assertEquals("bye", l.get(1));
        assertEquals("hello", l.get(2));
        assertEquals("how are you?", l.get(3));
    }

    @Test
    public void testNestedQuotes() {
        final List<String> l = (List<String>)parseExpression("[\"He said, \\\"Hello.\\\"\"]");
        assertEquals("He said, \"Hello.\"", l.get(0));
    }

    @Test
    public void testNestedQuotes2() {
        final List<String> l = (List<String>)parseExpression("[\"He said, \\\\\\\"Hello.\\\"\"]");
        assertEquals("He said, \\\"Hello.\"", l.get(0));
    }

    @Test
    public void testCommaList() {
        final List<String> l = (List<String>)parseExpression("[1, 2, 3 4]");
        assertEquals(1l, l.get(0));
        assertEquals(2l, l.get(1));
        assertEquals(3l, l.get(2));
        assertEquals(4l, l.get(3));
    }
}
