/**
 * Copyright (c) 2023 Sam Baskinger
 */
package com.github.basking2.sdsai.itrex.functions.function;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.Evaluator;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

public class HashArgsFunctionTest {
    @Test
    public void hashArgsFunctionWorks() {
        final Evaluator e = new Evaluator();
        final EvaluationContext ctx = e.getChildEvaluationContext();

        final HashArgsFunction f = (HashArgsFunction)ctx.getFunction("hashArgs");
        final Iterator<Object> remainingArgs = f.apply(
                asList("a:the letter a", "b:A colon value: b", new Object()).iterator(),
                ctx
        );

        assertSame(remainingArgs, ctx.getArguments());
        assertTrue(remainingArgs.hasNext());
        remainingArgs.next();
        assertFalse(remainingArgs.hasNext());
        assertEquals("the letter a", ctx.get("a"));
        assertEquals("A colon value: b", ctx.get("b"));
    }
}
