/**
 * Copyright (c) 2023 Sam Baskinger
 */
package com.github.basking2.sdsai.itrex.functions.functional;

import com.github.basking2.sdsai.itrex.Evaluator;
import org.junit.jupiter.api.Test;

import static com.github.basking2.sdsai.itrex.SimpleExpressionParser.parseExpression;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PipelineFunctionTest {
    @Test
    public void testPipeline() {
        final Evaluator e = new Evaluator();
        e.register("add", (itr, ctx) -> Integer.valueOf(itr.next().toString()) + Integer.valueOf(itr.next().toString()));


        Integer i = (Integer) e.evaluate(
                parseExpression(
                        "[[pipeline [curry add 1] [curry add 2]] 10] "
                ));

        assertEquals(Integer.valueOf(13), i);
    }
}
