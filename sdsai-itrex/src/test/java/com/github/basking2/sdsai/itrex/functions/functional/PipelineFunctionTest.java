package com.github.basking2.sdsai.itrex.functions.functional;

import com.github.basking2.sdsai.itrex.Evaluator;
import org.junit.Test;

import static com.github.basking2.sdsai.itrex.SimpleExpressionParser.parseExpression;
import static org.junit.Assert.assertEquals;

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
