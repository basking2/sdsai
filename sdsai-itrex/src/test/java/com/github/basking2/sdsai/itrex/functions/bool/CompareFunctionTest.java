package com.github.basking2.sdsai.itrex.functions.bool;

import com.github.basking2.sdsai.itrex.Evaluator;
import org.junit.Test;

import static com.github.basking2.sdsai.itrex.SimpleExpressionParser.parseExpression;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 */
public class CompareFunctionTest {
    @Test
    public void testGt() {
        final Evaluator evaluator = new Evaluator();

        assertTrue((Boolean)evaluator.evaluate(parseExpression("[gt 5 4]")));
        assertFalse((Boolean)evaluator.evaluate(parseExpression("[gt 4 4]")));
        assertTrue((Boolean)evaluator.evaluate(parseExpression("[gte 4 4]")));
        assertTrue((Boolean)evaluator.evaluate(parseExpression("[gt 5 4 3]")));
        assertFalse((Boolean)evaluator.evaluate(parseExpression("[gt 5 4 9]")));
    }

    @Test
    public void testLt() {
        final Evaluator evaluator = new Evaluator();

        assertTrue((Boolean)evaluator.evaluate(parseExpression("[lt 3 4]")));
        assertFalse((Boolean)evaluator.evaluate(parseExpression("[lt 4 4]")));
        assertTrue((Boolean)evaluator.evaluate(parseExpression("[lte 4 4]")));
        assertTrue((Boolean)evaluator.evaluate(parseExpression("[lt 3 4 5]")));
        assertFalse((Boolean)evaluator.evaluate(parseExpression("[lt 3 4 1]")));
    }

    @Test
    public void testEq() {
        final Evaluator evaluator = new Evaluator();

        assertTrue((Boolean)evaluator.evaluate(parseExpression("[eq 4 4]")));
        assertFalse((Boolean)evaluator.evaluate(parseExpression("[eq 3 4]")));
    }
}
