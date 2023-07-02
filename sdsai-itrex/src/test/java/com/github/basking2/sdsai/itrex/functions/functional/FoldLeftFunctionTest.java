/**
 * Copyright (c) 2023 Sam Baskinger
 */
package com.github.basking2.sdsai.itrex.functions.functional;

import com.github.basking2.sdsai.itrex.Evaluator;
import org.junit.jupiter.api.Test;

import static com.github.basking2.sdsai.itrex.SimpleExpressionParser.parseExpression;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by sskinger on 7/7/17.
 */
public class FoldLeftFunctionTest {
    @Test
    public void testFor() {
        final Evaluator e = new Evaluator();
        e.register("add", (itr, ctx) -> Integer.valueOf(itr.next().toString()) + Integer.valueOf(itr.next().toString()));


        Integer i = (Integer) e.evaluate(
                parseExpression(
                        "[fold" +
                        "   [curry add] " +
                        "   0 " +
                        "   [list 1 2 3 4]]"
                ));

        assertEquals(Integer.valueOf(10), i);
    }
}
