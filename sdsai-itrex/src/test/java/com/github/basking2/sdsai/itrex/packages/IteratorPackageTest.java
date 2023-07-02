/**
 * Copyright (c) 2023 Sam Baskinger
 */
package com.github.basking2.sdsai.itrex.packages;

import com.github.basking2.sdsai.itrex.Evaluator;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static com.github.basking2.sdsai.itrex.iterators.Iterators.wrap;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 */
public class IteratorPackageTest {
    @Test
    public void testFlatten2() {
        final Evaluator evaluator = new Evaluator();

        final Iterator<Integer> i = (Iterator<Integer>) evaluator.evaluate(
                wrap("flatten2", wrap("list", wrap("list", 1,2), wrap("list", 3,4)), wrap("list", wrap("list", 5, 6)))
        );

        assertEquals(1, i.next().intValue());
        assertEquals(2, i.next().intValue());
        assertEquals(3, i.next().intValue());
        assertEquals(4, i.next().intValue());
        assertEquals(5, i.next().intValue());
        assertEquals(6, i.next().intValue());
    }
}
