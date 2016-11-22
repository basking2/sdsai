package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.Evaluator;
import com.github.basking2.sdsai.itrex.SimpleExpressionParser;
import org.junit.Test;

import java.beans.Expression;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.util.Arrays.asList;
import static java.util.Arrays.sort;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test {@link ThreadFunction} and {@link JoinFunction}.
 */
public class ThreadJoinTest {

    @Test
    public void testThreadJoin() {
        final Evaluator evaluator = new Evaluator();

        ConcurrentLinkedQueue<Long> q = new ConcurrentLinkedQueue<Long>();
        q.add(1l);
        q.add(2l);
        q.add(3l);
        q.add(4l);
        q.add(5l);
        q.add(6l);
        q.add(7l);
        q.add(8l);
        q.add(9l);
        q.add(10l);

        final EvaluationContext ctx = new EvaluationContext();
        ctx.set("q", q);

        final Object expression = SimpleExpressionParser.parseExpression(
            "[join [thread [get q]]]"
        );

        final Iterator<Long> results = (Iterator<Long>) evaluator.evaluate(expression, ctx);

        final SortedSet<Long> sortedSet = new TreeSet<>();

        while (results.hasNext()) {
            sortedSet.add(results.next());
        }

        assertTrue(sortedSet.contains(1l));
        assertTrue(sortedSet.contains(2l));
        assertTrue(sortedSet.contains(3l));
        assertTrue(sortedSet.contains(4l));
        assertTrue(sortedSet.contains(5l));
        assertTrue(sortedSet.contains(6l));
        assertTrue(sortedSet.contains(7l));
        assertTrue(sortedSet.contains(8l));
        assertTrue(sortedSet.contains(9l));
        assertTrue(sortedSet.contains(10l));

    }
}
