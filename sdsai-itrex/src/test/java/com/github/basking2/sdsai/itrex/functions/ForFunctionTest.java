/**
 * Copyright (c) 2023 Sam Baskinger
 */
package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.Evaluator;
import org.junit.jupiter.api.Test;

import static com.github.basking2.sdsai.itrex.SimpleExpressionParser.parseExpression;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 */
public class ForFunctionTest {
    @Test
    public void testFor() {
        final Evaluator e = new Evaluator();
        e.register("add", (itr, ctx) -> (Integer)itr.next() + (Integer)itr.next());


        Integer i = (Integer) e.evaluate(
            parseExpression(
                    "[last" +
                    "   [set i 0] " +
                    "   [for j [list 1 2 3 4] [set i [add [get i] [get j]]]]] "
            ));

        assertEquals(Integer.valueOf(10), i);
    }
}
