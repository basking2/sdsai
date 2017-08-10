package com.github.basking2.sdsai.itrex.functions.java;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.Evaluator;

import com.github.basking2.sdsai.itrex.SimpleExpressionParser;
import com.github.basking2.sdsai.itrex.packages.JavaPackage;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static com.github.basking2.sdsai.itrex.SimpleExpressionParser.parseExpression;
import static java.util.Arrays.asList;

public class JavaFunctionTest {
    @Test
    public void testSimpleJavaObject() {
        final Evaluator e = new Evaluator();
        final EvaluationContext ctx = e.getRootEvaluationContext();

        // Import java package.
        e.evaluate(asList("import", JavaPackage.class.getCanonicalName()), ctx);

        final String al = (String)e.evaluate(parseExpression("[javaNew [classOf java.lang.String] a-test]"), ctx);
        assertEquals("a-test", al);

    }

    @Test
    public void testObjectMethods() {
        final Evaluator e = new Evaluator();
        final EvaluationContext ctx = e.getRootEvaluationContext();

        // Import java package.
        e.evaluate(asList("import", JavaPackage.class.getCanonicalName()), ctx);

        final ArrayList<Object> al = (ArrayList<Object>)e.evaluate(parseExpression("[set al [javaNew [classOf java.util.ArrayList] 3]]"), ctx);

        assertEquals(0, al.size());

        e.evaluate(parseExpression("[java add [get al] foo]"), ctx);
        e.evaluate(parseExpression("[java add [get al] bar]"), ctx);

        assertEquals(2, al.size());
        assertEquals("foo", al.get(0));
        assertEquals("bar", al.get(1));
        assertEquals(al.get(0), (String)e.evaluate(parseExpression("[java get [get al] 0]"), ctx));
        assertEquals(al.get(1), (String)e.evaluate(parseExpression("[java get [get al] 1]"), ctx));
    }
}
