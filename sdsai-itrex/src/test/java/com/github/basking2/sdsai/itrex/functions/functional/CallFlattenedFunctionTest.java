package com.github.basking2.sdsai.itrex.functions.functional;

import com.github.basking2.sdsai.itrex.Evaluator;
import org.junit.Test;

import static com.github.basking2.sdsai.itrex.SimpleExpressionParser.parseExpression;
import static org.junit.Assert.assertEquals;

public class CallFlattenedFunctionTest {
    @Test
    public void testCallFlattenedFunction() {
        final Evaluator e = new Evaluator();
        e.register("add", (itr, ctx) -> Integer.valueOf(itr.next().toString()) + Integer.valueOf(itr.next().toString()));


        Integer i = (Integer) e.evaluate(
                parseExpression("[callFlattened [function [head [tail [tail [tail [args]]]]]] 1 2 [list 3 4]]"));

        assertEquals(Integer.valueOf(4), i);
    }

    @Test
    public void testCallFlattenedFunction2() {
        final Evaluator e = new Evaluator();
        e.register("add", (itr, ctx) -> Integer.valueOf(itr.next().toString()) + Integer.valueOf(itr.next().toString()));


        Integer i = (Integer) e.evaluate(
                parseExpression("[callFlattened [function [last [arg] [arg] [arg] [arg] [arg]]] 1 2 [list 3 4] 5]"));

        assertEquals(Integer.valueOf(5), i);
    }
}
