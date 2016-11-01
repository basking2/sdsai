package com.github.basking2.sdsai.sexpr;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
}
