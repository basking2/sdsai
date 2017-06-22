package com.github.basking2.sdsai.itrex;

import com.github.basking2.sdsai.itrex.functions.Functions;
import com.github.basking2.sdsai.itrex.functions.LogFunction;
import com.github.basking2.sdsai.itrex.packages.JavaMathPackage;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.github.basking2.sdsai.itrex.SimpleExpressionParser.parseExpression;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class JavaMathEvaluatorTest {
    @Test
    public void testAdding() {
        final Evaluator e = new Evaluator();
        new JavaMathPackage().importTo(e);
        final Integer i = (Integer)e.evaluate(parseExpression("[abs -1]"));
        assertEquals(Integer.valueOf(1), i);
    }

    @Test
    public void testAddingFloat() {
        final Evaluator e = new Evaluator();
        new JavaMathPackage().importTo(e);
        final Float i = (Float)e.evaluate(parseExpression("[abs [toFloat -1]]"));
        assertEquals(Float.valueOf(1), i);
    }

    @Test
    public void testMax() {
        final Evaluator e = new Evaluator();
        new JavaMathPackage().importTo(e);
        final Double i = (Double)e.evaluate(parseExpression("[abs [max [toDouble 1] 3d]]"));
        assertEquals(Double.valueOf(3), i);
    }

    @Test
    public void testMaxLongAndDouble() {
        final Evaluator e = new Evaluator();
        new JavaMathPackage().importTo(e);
        final Double i = (Double)e.evaluate(parseExpression("[abs [max 1l 3d]]"));
        assertEquals(Double.valueOf(3), i);
    }

    @Test
    public void testRound() {
        final Evaluator e = new Evaluator();
        new JavaMathPackage().importTo(e);
        final Long i = (Long)e.evaluate(parseExpression("[round 1.1]"));

        assertEquals(Long.valueOf(1), i);
    }

    @Test
    public void testImportMath() {
        final Evaluator e = new Evaluator();

        e.evaluate(parseExpression("[import " + JavaMathPackage.class.getCanonicalName() + "]"));
        final Long i = (Long)e.evaluate(parseExpression("[round 1.1]"));

        assertEquals(Long.valueOf(1), i);
    }

    @Test
    public void testMapRound() {
        final Evaluator e = new Evaluator();
        new JavaMathPackage().importTo(e);
        final Iterator<Long> i = (Iterator<Long>)e.evaluate(parseExpression("[map [curry round] [list 1.1 1.2]]"));

        assertEquals(Long.valueOf(1), i.next());
        assertEquals(Long.valueOf(1), i.next());
    }
    @Test
    public void testMapCurry() {
        final Evaluator e = new Evaluator();
        new JavaMathPackage().importTo(e);
        final Iterator<Long> i = (Iterator<Long>)e.evaluate(parseExpression("[map [curry max 2.3] [list 1.1 1.2 5]]"));

        assertEquals(Double.valueOf(2.3), i.next());
        assertEquals(Double.valueOf(2.3), i.next());
        assertEquals(Double.valueOf(5), i.next());
    }

    @Test
    public void testMapCompose() {
        final Evaluator e = new Evaluator();
        new JavaMathPackage().importTo(e);
        final Iterator<Long> i = (Iterator<Long>)e.evaluate(parseExpression("[map [compose [curry round] [curry max 2.3]] [list 1.1 1.2 5]]"));

        assertEquals(Long.valueOf(2), i.next());
        assertEquals(Long.valueOf(2), i.next());
        assertEquals(Long.valueOf(5), i.next());
    }
}
