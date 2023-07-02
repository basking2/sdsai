/**
 * Copyright (c) 2023 Sam Baskinger
 */
package com.github.basking2.sdsai.itrex;

import static com.github.basking2.sdsai.itrex.SimpleExpressionParser.parseExpression;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.basking2.sdsai.itrex.functions.LogFunction;

import com.github.basking2.sdsai.itrex.functions.Functions;
import org.junit.jupiter.api.Test;

public class EvaluatorTest {
    @Test
    public void testAdding() {
        final List<Object> argOrder = new ArrayList<>();
        Evaluator evaluator = new Evaluator();
        evaluator.register("add", (iterator, ctx) -> {

            double sum = 0;

            while (iterator.hasNext()) {
                final Object arg = iterator.next();
                argOrder.add(arg);
                if (arg instanceof Integer) {
                    sum += (Integer)arg;
                }

                if (arg instanceof Float) {
                    sum += (Float)arg;
                }

                if (arg instanceof Double) {
                    sum += (Double)arg;
                }

                if (arg instanceof String) {
                    sum += Double.valueOf((String)arg);
                }
            }

            return sum;
        });


        final List<Object> l = asList("add", 1, asList("add", 0, 32D), asList("add", 1, 1));

        final Object o = evaluator.evaluate(l, evaluator.getChildEvaluationContext());

        assertTrue(o instanceof Double);
        assertEquals(Double.valueOf(35), o);
        assertEquals(Integer.valueOf(1), argOrder.get(0));
        assertEquals(Integer.valueOf(0), argOrder.get(1));
        assertEquals(Double.valueOf(32), argOrder.get(2));
        assertEquals(Double.valueOf(32), argOrder.get(3));
        assertEquals(Integer.valueOf(1), argOrder.get(4));
        assertEquals(Integer.valueOf(1), argOrder.get(5));
        assertEquals(Double.valueOf(2), argOrder.get(6));
    }

    @Test
    public void testMap() {
        Evaluator evaluator = new Evaluator();
        evaluator.register("add", (iterator, ctx) -> {
            int sum = 0;

            while (iterator.hasNext()) {
                sum += (Integer) iterator.next();
            }

            return sum;
        });


        final List<Object> l = asList("map", asList("curry", "add", 3), asList("list", 4, 5));

        @SuppressWarnings("unchecked")
        Iterator<Iterator<Integer>> i = (Iterator<Iterator<Integer>>) evaluator.evaluate(l, evaluator.getChildEvaluationContext());

        assertEquals(Integer.valueOf(7), i.next());
        assertEquals(Integer.valueOf(8), i.next());
        assertFalse(i.hasNext());

    }

    @Test
    public void testList() {
        Evaluator evaluator = new Evaluator();

        final List<Object> l = asList("list", 1, 2, 3, 4, 5);

        @SuppressWarnings("unchecked")
        List<Integer> i = (List<Integer>) evaluator.evaluate(l, evaluator.getChildEvaluationContext());

        assertEquals(5, i.size());
        assertEquals(Integer.valueOf(1), i.get(0));
        assertEquals(Integer.valueOf(2), i.get(1));
        assertEquals(Integer.valueOf(3), i.get(2));
        assertEquals(Integer.valueOf(4), i.get(3));
        assertEquals(Integer.valueOf(5), i.get(4));

    }
    
    @Test
    public void testLastFunction() {
        final Evaluator evaluator = new Evaluator();

        final List<Object> l = asList("last", 1, 2, 3, 4, 5);

        Integer i = (Integer) evaluator.evaluate(l, evaluator.getChildEvaluationContext());

        assertEquals(Integer.valueOf(5), i);
    }
    
    @Test
    public void testIfFunction() {
        final Evaluator evaluator = new Evaluator();
        
        Integer i;

        i = (Integer) evaluator.evaluate(asList("if", 0, 2, 3), evaluator.getChildEvaluationContext());
        assertEquals(Integer.valueOf(3), i);

        i = (Integer) evaluator.evaluate(asList("if", 1, 2, 3), evaluator.getChildEvaluationContext());
        assertEquals(Integer.valueOf(2), i);
    }

    @Test
    public void testCompose() {
        final Evaluator evaluator = new Evaluator();
        evaluator.register("add", (iterator, ctx) -> {
            int sum = 0;

            while (iterator.hasNext()) {
                sum += (Integer) iterator.next();
            }

            return sum;
        });
        @SuppressWarnings("unchecked")
        Iterator<Integer> i = (Iterator<Integer>)
                evaluator.evaluate(asList("map", asList("compose", asList("curry", "add", 1), asList("curry", "add", 2)), asList("list", 3, 4, 5)), evaluator.getChildEvaluationContext());

        assertEquals(Integer.valueOf(6), i.next());
        assertEquals(Integer.valueOf(7), i.next());
        assertEquals(Integer.valueOf(8), i.next());
        assertFalse(i.hasNext());
    }

    @Test
    public void testCompose2() {
        final Evaluator evaluator = new Evaluator();

        final Object expression = parseExpression("[toString [list a b]]");

        @SuppressWarnings("unchecked")
        String result = (String) evaluator.evaluate(expression);

        System.out.println(result);
    }

    @Test
    public void testFlatten() {
        final Evaluator evaluator = new Evaluator();
        @SuppressWarnings("unchecked")
        Iterator<Integer> i = (Iterator<Integer>)
                evaluator.evaluate(asList("flatten", asList("list", 1, 2, 3), asList("list", 4, 5, 6)), evaluator.getChildEvaluationContext());

        assertEquals(Integer.valueOf(1), i.next());
        assertEquals(Integer.valueOf(2), i.next());
        assertEquals(Integer.valueOf(3), i.next());
        assertEquals(Integer.valueOf(4), i.next());
        assertEquals(Integer.valueOf(5), i.next());
        assertEquals(Integer.valueOf(6), i.next());
        assertFalse(i.hasNext());
    }
    
    @Test
    public void testAggregator() {
        final Evaluator evaluator = new Evaluator();
        evaluator.register(
            "add",
            Functions.aggregator(Integer.valueOf(0),  (r, t) -> { return r + (Integer)t; })
        );
        
        final Integer i = (Integer)evaluator.evaluate(
                asList("add", asList("list", 1,2,3), 4), evaluator.getChildEvaluationContext());
        
        assertEquals(Integer.valueOf(10), i);
    }

    @Test
    public void testLogging() {
        final Evaluator evaluator = new Evaluator();
        for (LogFunction.LEVEL l : LogFunction.LEVEL.values()) {
            final String function = "log."+l.toString().substring(0, 1).toLowerCase() + l.toString().substring(1).toLowerCase();
            evaluator.evaluate(asList(function, l));
        }
    }

    @Test
    public void testStrings() {
        final Evaluator evaluator = new Evaluator();

        @SuppressWarnings("unchecked")
        List<String> l = (List<String>) evaluator.evaluate(
            asList(
                    "string.split",
                    ",",
                    asList("string.join", ",",
                        asList("string.concat", "a", ":", "b"),
                        asList("string.concat", "c", ":", "d"))));

        assertEquals(2, l.size());
        assertEquals("a:b", l.get(0));
        assertEquals("c:d", l.get(1));
    }

    @Test
    public void testBoolean() {
        final Evaluator e = new Evaluator();

        assertTrue((Boolean)e.evaluate(parseExpression("[and]")));
        assertFalse((Boolean)e.evaluate(parseExpression("[or]")));

        assertFalse((Boolean)e.evaluate(parseExpression("[not [and]]")));
        assertTrue((Boolean)e.evaluate(parseExpression("[not [or]]")));
        assertTrue((Boolean)e.evaluate(parseExpression("[not [and] [or]]")));

        assertTrue((Boolean)e.evaluate(parseExpression("[or 1 2]")));
        assertFalse((Boolean)e.evaluate(parseExpression("[and [not [and]] 2]")));

        assertFalse((Boolean)e.evaluate(parseExpression("[not astring]")));
    }

    @Test
    public void testFunction() {
        final Evaluator e = new Evaluator();
        assertFalse((Boolean)e.evaluate((parseExpression("[[function [hasArg]]]"))));

        assertTrue((Boolean)e.evaluate((parseExpression("[[function [hasArg]] hi]"))));

        assertEquals("hi", (String)e.evaluate((parseExpression("[[function [arg]] hi]"))));
        assertEquals("hi", (String)e.evaluate((parseExpression("[[function [if [hasArg] [arg] []]] hi]"))));
        assertEquals("hi", (String)e.evaluate((
                parseExpression(
                        "[let "+
                        "    [* This is the comment block? Yup. It is. " +
                       " And it's [] here [hi let] no!" +
                        "*] " +
                        "[* And a second comment. *]" +
                        "    [set f [function [if [hasArg] [arg] []]]] "+
                        "     [[get f] hi]"+
                        "]"
                ))));
    }

    @Test
    public void testCrash01() {
        final Evaluator e = new Evaluator();
        final Object o = parseExpression("[string.join \",\n\" [map [curry toString] [range 0 101 1]]]");
        e.evaluate(o);
    }

    @Test
    public void testNamedFunction() {
        final Evaluator e = new Evaluator();
        final Object o = parseExpression("[let \n"+
                "[register f [curry toString]]\n" +
                "[f 4.1d]" +
                "]");
        final String s = e.evaluate(o).toString();

        assertEquals("4.1", s);
    }

    @Test
    public void testNamedFunction2() {
        final Evaluator e = new Evaluator();
        final Object o = parseExpression("[let \n"+
                "[register f [function [toString [arg]]]]\n" +
                "[f 4.1d]" +
                "]");
        final String s = e.evaluate(o).toString();

        assertEquals("4.1", s);
    }

    @Test
    public void testNamedFunction3() {
        final Evaluator e = new Evaluator();
        final Object o = parseExpression("[let \n"+
                "[fn f [toString [arg]]]\n" +
                "[f 4.1d]" +
                "]");
        final String s = e.evaluate(o).toString();

        assertEquals("4.1", s);
    }

    @Test
    public void testFunctionScoping() {
        final Evaluator e = new Evaluator();
        final Object o = parseExpression("[last \n"+
                "[set t 1]\n"+
                "[register f [function [toString [get t]]]]\n" +
                "[set t 2]\n"+
                "[f]" +
                "]");
        final String s = e.evaluate(o).toString();

        assertEquals("2", s);
    }

    @Test
    public void testNameArgs() {
        final Evaluator e = new Evaluator();
        final EvaluationContext ctx = e.getChildEvaluationContext();
        e.evaluate(parseExpression("[[function [nameArgs a b c d]] a b c d e]"), ctx);
        assertEquals("a", ctx.get("a").toString());
        assertEquals("b", ctx.get("b").toString());
        assertEquals("c", ctx.get("c").toString());
        assertEquals("d", ctx.get("d").toString());
        assertNull(ctx.get("e"));

    }

    @Test
    public void testIsItr() {
        final Evaluator e = new Evaluator();
        final EvaluationContext ctx = e.getChildEvaluationContext();
        assertEquals(Boolean.TRUE, e.evaluate(parseExpression("[isitr []]")));
        assertEquals(Boolean.TRUE, e.evaluate(parseExpression("[isitr [map [function [arg]] []]]")));
        assertEquals(Boolean.TRUE, e.evaluate(parseExpression("[isitr [map [function [arg]] [list 1,2,3]]]")));
        assertEquals(Boolean.FALSE, e.evaluate(parseExpression("[isitr hi]")));
        assertEquals(Boolean.FALSE, e.evaluate(parseExpression("[isitr [fn if]]")));
        assertEquals(Boolean.FALSE, e.evaluate(parseExpression("[isitr 1]")));
    }

    @Test
    public void testFilter() {
        final Evaluator e = new Evaluator();
        final EvaluationContext ctx = e.getChildEvaluationContext();
        final Iterator<Object> i1 = (Iterator<Object>) e.evaluate(parseExpression("[filter [curry eq a] [list b c d a q a] ]"));
        assertTrue(i1.hasNext());
        assertEquals("a", (String)i1.next());

        assertTrue(i1.hasNext());
        assertEquals("a", (String)i1.next());

        assertFalse(i1.hasNext());
    }
}
