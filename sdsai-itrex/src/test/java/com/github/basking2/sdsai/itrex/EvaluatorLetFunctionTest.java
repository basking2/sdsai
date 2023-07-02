/**
 * Copyright (c) 2023 Sam Baskinger
 */
package com.github.basking2.sdsai.itrex;

import com.github.basking2.sdsai.itrex.functions.LetFunction;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Because the {@link LetFunction} is complicated, we isolate its tests into a class.
 */
public class EvaluatorLetFunctionTest {
    @Test
    public void testSimpleLet() {
        Evaluator evaluator = new Evaluator();
        evaluator.register("add", (itr, ctx) -> {
            int i = 0;
            while (itr.hasNext()) {
                i += (Integer)itr.next();
            }
            return i;
        });

        Object o = asList("let",
            asList("set", "a", 1),
            asList("set", "b", 2),
            asList("add",
                asList("get", "a"),
                asList("get", "b")
            )
        );
        Object r = evaluator.evaluate(o);

        assertEquals(Integer.valueOf(3), (Integer)r);
    }

    @Test
    public void testScoping() {
        Evaluator evaluator = new Evaluator();
        evaluator.register("add", (itr, ctx) -> {
            int i = 0;
            while (itr.hasNext()) {
                i += (Integer)itr.next();
            }
            return i;
        });

        Object o =
            asList("let",
                asList("set", "a", 1),
                asList("set", "b", 2),
                // Just before calling add, set a new scope and set a to 20.
                asList("let", asList("set", "a", 20)),
                asList("add",
                        asList("get", "a"),
                        asList("get", "b")
                )
        );
        Object r = evaluator.evaluate(o);

        assertEquals(Integer.valueOf(3), (Integer)r);
    }
}
