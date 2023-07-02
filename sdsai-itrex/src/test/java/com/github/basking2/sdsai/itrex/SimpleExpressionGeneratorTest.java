/**
 * Copyright (c) 2023 Sam Baskinger
 */
package com.github.basking2.sdsai.itrex;

import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimpleExpressionGeneratorTest {
    @Test
    public void print01() {
        final Object expr = asList("hello \\ this is a string", asList(1, 2l, 3f, 4d));

        final String str = new SimpleExpressionGenerator(expr).generate();

        // System.out.println(str);

        final Object expr2 = new SimpleExpressionParser(str).parse();

        final String str2 = new SimpleExpressionGenerator(expr2).generate();

        assertEquals(str, str2);
    }

    @Test
    public void prettyPrint01() {
        final Object expr = asList("hello \\ this is a string", asList(1, 2l, 3f, 4d));

        final String str = new SimpleExpressionGenerator(expr, "  ").generate();

        // System.out.println(str);

        final Object expr2 = new SimpleExpressionParser(str).parse();

        final String str2 = new SimpleExpressionGenerator(expr2, "  ").generate();

        assertEquals(str, str2);
    }
}
