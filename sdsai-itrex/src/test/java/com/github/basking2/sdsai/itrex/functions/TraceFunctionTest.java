/**
 * Copyright (c) 2023 Sam Baskinger
 */
package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.Evaluator;
import com.github.basking2.sdsai.itrex.SimpleExpressionParser;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TraceFunctionTest {
    @Test
    public void testTrace() {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream ps = new PrintStream(baos);
        final Evaluator evaluator = new Evaluator();
        final Object expression = new SimpleExpressionParser("[trace if [t] hi bye]").parse();
        final TraceFunction trace = new TraceFunction(ps);
        evaluator.register("trace", trace);
        final Object results = evaluator.evaluate(expression);
        ps.close();

        final String traceOutput = new String(baos.toByteArray());

        // Check we actually print the trace output.
        assertEquals(traceOutput, "[ if true hi bye ]\n");

        // Make sure the function actually evaluated.
        assertEquals("hi", results.toString());
    }
}
