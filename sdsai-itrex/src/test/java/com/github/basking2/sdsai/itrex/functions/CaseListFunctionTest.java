package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.Evaluator;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

public class CaseListFunctionTest {
    @Test
    public void caseListFunctionNull() {
        final Evaluator e = new Evaluator();
        final CaseListFunction caseListFunction = new CaseListFunction();

        assertNull(e.evaluate(asList(caseListFunction)));
    }

    @Test
    public void caseListFunctionFind1() {
        final Evaluator e = new Evaluator();
        final EvaluationContext ctx = e.getChildEvaluationContext();

        final Object o = e.evaluate(asList(
                "caseList"
                , asList("case", false, asList("set", "a", "a"))
                , asList("case", true, asList("set", "a", "b"))
                , asList("case", false, asList("set", "a", "c"))
        ), ctx);

        // Test that caseList returns the found value.
        assertEquals("b", o);

        // Test that the case list does not execute the last clause, [set a c].
        assertEquals("b", ctx.get("a"));
    }
}
