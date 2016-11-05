package com.github.basking2.sdsai.sexpr;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class EvaluatorTest {
    @Test
    public void testAdding() {
        final List<Object> argOrder = new ArrayList<>();
        Evaluator evaluator = new Evaluator();
        evaluator.register("add", iterator -> {

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

        final Object o = evaluator.evaluate(l);

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
        evaluator.register("add", iterator -> {
            int sum = 0;

            while (iterator.hasNext()) {
                sum += (Integer) iterator.next();
            }

            return sum;
        });


        final List<Object> l = asList("map", asList("curry", "add", 3), 4, 5);

        @SuppressWarnings("unchecked")
        Iterator<Iterator<Integer>> i = (Iterator<Iterator<Integer>>) evaluator.evaluate(l);

        assertEquals(Integer.valueOf(7), i.next());
        assertEquals(Integer.valueOf(8), i.next());
        assertFalse(i.hasNext());

    }

    @Test
    public void testList() {
        Evaluator evaluator = new Evaluator();

        final List<Object> l = asList("list", 1, 2, 3, 4, 5);

        @SuppressWarnings("unchecked")
        Iterator<Integer> i = (Iterator<Integer>) evaluator.evaluate(l);

        assertEquals(Integer.valueOf(1), i.next());
        assertEquals(Integer.valueOf(2), i.next());
        assertEquals(Integer.valueOf(3), i.next());
        assertEquals(Integer.valueOf(4), i.next());
        assertEquals(Integer.valueOf(5), i.next());
        assertFalse(i.hasNext());

    }
    
    @Test
    public void testLastFunction() {
        final Evaluator evaluator = new Evaluator();

        final List<Object> l = asList("last", 1, 2, 3, 4, 5);

        Integer i = (Integer) evaluator.evaluate(l);

        assertEquals(Integer.valueOf(5), i);
    }
    
    @Test
    public void testIfFunction() {
        final Evaluator evaluator = new Evaluator();
        
        Integer i;

        i = (Integer) evaluator.evaluate(asList("if", 0, 2, 3));
        assertEquals(Integer.valueOf(3), i);

        i = (Integer) evaluator.evaluate(asList("if", 1, 2, 3));
        assertEquals(Integer.valueOf(2), i);
    }
}