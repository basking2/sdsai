package com.github.basking2.sdsai.itrex;

import static com.github.basking2.sdsai.itrex.SimpleExpressionParser.parseExpression;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.Evaluator;
import com.github.basking2.sdsai.itrex.functions.LogFunction;
import org.junit.Test;

import com.github.basking2.sdsai.itrex.functions.Functions;

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
            final String function = "log"+l.toString().substring(0, 1) + l.toString().substring(1).toLowerCase();
            evaluator.evaluate(asList(function, l));
        }
    }

    @Test
    public void testStrings() {
        final Evaluator evaluator = new Evaluator();

        @SuppressWarnings("unchecked")
        List<String> l = (List<String>) evaluator.evaluate(
            asList(
                    "stringSplit",
                    ",",
                    asList("stringJoin", ",",
                        asList("stringConcat", "a", ":", "b"),
                        asList("stringConcat", "c", ":", "d"))));

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
                        "    [set f [function [if [hasArg] [arg] []]]] "+
                        "     [[get f] hi]"+
                        "]"
                ))));
    }
}
