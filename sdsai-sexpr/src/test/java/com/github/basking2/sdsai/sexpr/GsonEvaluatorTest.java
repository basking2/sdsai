package com.github.basking2.sdsai.sexpr;

import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class GsonEvaluatorTest {
    @Test
    public void testGsonEvaluation() {

        GsonEvaluator evaluator = new GsonEvaluator();
        evaluator.register("add", iterator -> {

            double sum = 0;

            while (iterator.hasNext()) {
                final Object arg = iterator.next();
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


        final Object o = evaluator.evaluateJson("[\"add\", 1, [\"add\", 0, 32], [\"add\", 1, 1]]");

        assertTrue(o instanceof Double);
        assertEquals(Double.valueOf(35), o);
    }
}
