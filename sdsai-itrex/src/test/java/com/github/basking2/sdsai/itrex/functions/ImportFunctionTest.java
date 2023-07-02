/**
 * Copyright (c) 2023 Sam Baskinger
 */
package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.Evaluator;
import org.junit.jupiter.api.Test;

import static com.github.basking2.sdsai.itrex.SimpleExpressionParser.parseExpression;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ImportFunctionTest {

    public static class MyPackage {
        public static final String __package = "foo";
        public static final FunctionInterface<Object> foo = (args, ctx) -> "foo";
    }


    @Test
    public void testImportFoo() {
        final Evaluator e = new Evaluator();
        final EvaluationContext ctx = e.getChildEvaluationContext();
        e.evaluate(parseExpression(String.format("[import \"%s\"]", MyPackage.class.getName())), ctx);
        assertEquals("foo", e.evaluate(parseExpression("[foo.foo]"), ctx));
    }

    @Test
    public void testImportToAnotherPackage() {
        final Evaluator e = new Evaluator();
        final EvaluationContext ctx = e.getChildEvaluationContext();
        e.evaluate(parseExpression(String.format("[import \"%s\" as bar]", MyPackage.class.getName())), ctx);
        assertEquals("foo", e.evaluate(parseExpression("[bar.foo]"), ctx));
    }

    @Test
    public void testImportToRootPackage() {
        final Evaluator e = new Evaluator();
        final EvaluationContext ctx = e.getChildEvaluationContext();
        e.evaluate(parseExpression(String.format("[import \"%s\" as \"\"]", MyPackage.class.getName())), ctx);
        assertEquals("foo", e.evaluate(parseExpression("[foo]"), ctx));
    }

    @Test
    public void testImportToRootPackage2() {
        final Evaluator e = new Evaluator();
        final EvaluationContext ctx = e.getChildEvaluationContext();
        e.evaluate(parseExpression(String.format("[import \"%s\" as]", MyPackage.class.getName())), ctx);
        assertEquals("foo", e.evaluate(parseExpression("[foo]"), ctx));
    }
}
