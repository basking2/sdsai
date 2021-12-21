/**
 * Copyright (c) 2021 Sam Baskinger
 */

package com.github.basking2.sdsai;

import org.junit.Test;

import static org.junit.Assert.*;

public class IntervalTest {
    @Test(expected = IllegalStateException.class)
    public void testIntervalNodesInvalidInterval() {
        new Interval<Integer>(2, 1);
    }

    @Test
    public void zeroSizeIntervalsContainNothing() {
        Interval<Integer> n1 = new Interval<>(0, 0);
        Interval<Integer> n2 = new Interval<>(0, 0);
        assertFalse(n1.contains(n2));
        assertFalse(n2.contains(n1));
    }

    @Test
    public void zeroSizeIntervalsMayEqual() {
        Interval<Integer> n1 = new Interval<>(0, 0);
        Interval<Integer> n2 = new Interval<>(0, 0);
        assertTrue(n1.containsOrEqual(n2));
        assertTrue(n2.containsOrEqual(n1));
        assertTrue(n1.equals(n2));
        assertTrue(n2.equals(n1));
    }

    @Test
    public void zeroSizedIntervalsOverlap() {
        Interval<Integer> n1 = new Interval<>(0, 0);
        Interval<Integer> n2 = new Interval<>(0, 0);
        assertTrue(n1.overlaps(n2));
        assertTrue(n2.overlaps(n1));
    }

    @Test
    public void testAboveBelow() {
        Interval<Integer> n1 = new Interval<>(0, 1);
        Interval<Integer> n2 = new Interval<>(1, 2);
        Interval<Integer> n3 = new Interval<>(0, 2);
        assertFalse(n1.above(n2));
        assertTrue(n2.above(n1));
        assertTrue(n1.below(n2));
        assertFalse(n2.below(n1));

        assertFalse(n1.above(n3));
        assertFalse(n2.above(n3));
        assertFalse(n1.below(n3));
        assertFalse(n2.below(n3));
        assertFalse(n3.above(n1));
        assertFalse(n3.above(n2));
        assertFalse(n3.below(n1));
        assertFalse(n3.below(n2));
    }

    @Test
    public void testPoint() {
        Interval<Integer> i = new Interval<>(1, 2);

        assertTrue(i.contains(1));
        assertFalse(i.contains(0));
        assertFalse(i.contains(2));
    }
}
