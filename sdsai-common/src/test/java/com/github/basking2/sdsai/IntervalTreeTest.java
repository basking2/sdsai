package com.github.basking2.sdsai;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
}
