/**
 * Copyright (c) 2023 Sam Baskinger
 */
package com.github.basking2.sdsai.itrex;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EvalItrmlFunctionTest {
    @Test
    public void testFoo() {
        final Evaluator evalator = new Evaluator();

        evalator.evaluate(new String[]{"evalItrml", "/foo.itrml"}, evalator.getRootEvaluationContext());

        final Object o = evalator.evaluate(new String[]{"foo"});

        assertEquals("foo", (String)o);
    }

    @Test
    public void testFoo2() {
        final Evaluator evalator = new Evaluator();

        final Object o = evalator.evaluate(new String[]{"evalItrml", "/foo2.itrml"});

        assertEquals("foo", (String)o);
    }
}
