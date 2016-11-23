package com.github.basking2.sdsai.itrex;

import static com.github.basking2.sdsai.itrex.SimpleExpressionParser.parseExpression;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

/**
 *
 */
public class SimpleExpressionParserTest {
    @Test
    public void testEmptyList() {
        @SuppressWarnings("unchecked")
        final List<Object> l = (List<Object>)parseExpression("[]");
        
        assertNotNull(l);
    }

    @Test
    public void testLongList() {
        @SuppressWarnings("unchecked")
        final List<Object> l = (List<Object>)parseExpression("[1  2l  3L]");
        assertEquals(3, l.size());
        assertEquals(Integer.valueOf(1), l.get(0));
        assertEquals(Long.valueOf(2), l.get(1));
        assertEquals(Long.valueOf(3), l.get(2));
    }

    @Test
    public void testLongListList() {
        @SuppressWarnings("unchecked")
        final List<List<Object>> l = (List<List<Object>>)parseExpression("[[1]  [2l  3L]]");
        assertEquals(2, l.size());
        assertEquals(Integer.valueOf(1), l.get(0).get(0));
        assertEquals(Long.valueOf(2), l.get(1).get(0));
        assertEquals(Long.valueOf(3), l.get(1).get(1));
    }

    @Test
    public void testDoubleListList() {
        @SuppressWarnings("unchecked")
        final List<List<Object>> l = (List<List<Object>>)parseExpression("[[1.0]  [2d  3D]]");
        assertEquals(2, l.size());
        assertEquals(Double.valueOf(1), l.get(0).get(0));
        assertEquals(Double.valueOf(2), l.get(1).get(0));
        assertEquals(Double.valueOf(3), l.get(1).get(1));
    }

    @Test
    public void testWords() {
        @SuppressWarnings("unchecked")
        final List<String> l = (List<String>)parseExpression("[hi bye hello \"how are you?\"]");
        assertEquals("hi", l.get(0));
        assertEquals("bye", l.get(1));
        assertEquals("hello", l.get(2));
        assertEquals("how are you?", l.get(3));
    }

    @Test
    public void testNestedQuotes() {
        @SuppressWarnings("unchecked")
        final List<String> l = (List<String>)parseExpression("[\"He said, \\\"Hello.\\\"\"]");
        assertEquals("He said, \"Hello.\"", l.get(0));
    }

    @Test
    public void testNestedQuotes2() {
        @SuppressWarnings("unchecked")
        final List<String> l = (List<String>)parseExpression("[\"He said, \\\\\\\"Hello.\\\"\"]");
        assertEquals("He said, \\\"Hello.\"", l.get(0));
    }

    @Test
    public void testCommaList() {
        @SuppressWarnings("unchecked")
        final List<String> l = (List<String>)parseExpression("[1, 2, 3 4]");
        assertEquals(1, l.get(0));
        assertEquals(2, l.get(1));
        assertEquals(3, l.get(2));
        assertEquals(4, l.get(3));
    }
    
    @Test
    public void testReturns() {
        @SuppressWarnings("unchecked")
        final List<List<String>> l = (List<List<String>>) parseExpression("[ [ hi\n\n ], \n\n\r\n [ bye ] \n]");
        
        assertEquals("hi", l.get(0).get(0));
        assertEquals("bye", l.get(1).get(0));
    }

}
