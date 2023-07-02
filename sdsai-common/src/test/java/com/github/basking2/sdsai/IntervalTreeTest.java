/**
 * Copyright (c) 2021-2023 Sam Baskinger
 */

package com.github.basking2.sdsai;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class IntervalTreeTest {
    @Test
    public void testAddRemoveDups() {
        final IntervalTree<Integer, Integer> it = new IntervalTree<>();

        it.add(new Interval<>(1, 2), 1);
        it.add(new Interval<>(1, 2), 2);

        assertEquals(it.size(), 2);
        assertEquals(it.remove(new Interval<>(1, 2)), Integer.valueOf(1));
        assertEquals(it.size(), 1);
        assertEquals(it.remove(new Interval<>(1, 2)), Integer.valueOf(2));
        assertEquals(it.size(), 0);
    }

    @Test
    public void testAddAndFind() {
        final IntervalTree<Integer, Integer> it = new IntervalTree<>();

        for (int i = 0; i < 100; i++) {
            it.add(new Interval<>(i, i+2), i+1);
        }

        Iterator<Integer> keys = it.keys();
        for (int i = 0; i < 100; i++) {
            assertTrue(keys.hasNext());
            final Integer j = keys.next();
            assertEquals(Integer.valueOf(i), j);
        }
        assertFalse(keys.hasNext());

        int size = it.size();

        while (true) {
            int i = (int)(Math.random() * 100);
            Integer j = it.remove(new Interval<>(i, i+2));
            if (j != null) {
                size--;
                assertEquals(size, it.size());
                assertEquals(j, Integer.valueOf(i+1));
            } else {
                assertEquals(size, it.size());
            }

            if (size == 0) {
                break;
            }
        }

    }

    @Test
    public void testFindOverlap() {
        final IntervalTree<Integer, Integer> it = new IntervalTree<>();

        // Intervals in this test.
        // 0 --------- 7
        // 0 - 2
        //     2 ---- 6
        //   1 -- 4
        //       3 - 5

        it.add(new Interval<>(0, 7), 1);
        it.add(new Interval<>(0, 2), 2);
        it.add(new Interval<>(2, 6), 3);
        it.add(new Interval<>(1, 4), 4);
        it.add(new Interval<>(3, 5), 5);

        final List<Integer> values = new ArrayList<>();

        it.findIntersecting(new Interval<>(1, 2), (k, v) -> values.add(v));

        assertEquals(values.get(0), Integer.valueOf(4));
        assertEquals(values.get(1), Integer.valueOf(2));
        assertEquals(values.get(2), Integer.valueOf(1));
    }
}
