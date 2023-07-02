/**
 * Copyright (c) 2018-2023 Sam Baskinger
 */

package com.github.basking2.sdsai;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class KDTreeTest {
    @Test
    public void testInsertsAndFinds() {
        final KDTree<String, String> kdTree = new KDTree<>();
        final int dimensions = 3;
        final int size = 100;

        final String[][] keys = new String[size][dimensions];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < dimensions; j++) {
                keys[i][j] = 1000*Math.random() +"";
            }

            kdTree.add(keys[i], i + "");
        }

        assertEquals(size, kdTree.size());
        assertFalse(kdTree.isEmpty());

        for (int i = 0; i < size; i++) {
            final String s = kdTree.find(keys[i]);
            assertEquals(i + "", s);
        }

        final String closeString = kdTree.findClosest(new String[]{
                1000*Math.random() +"",
                1000*Math.random() +"",
                1000*Math.random() +""
        });

        assertNotNull(closeString);

        assertNull(kdTree.find(new String[]{
                1000*Math.random() +"",
                1000*Math.random() +"",
                1000*Math.random() +""
        }));
    }

    @Test
    public void testMinsMaxes() {
        final KDTree<String, String> kdTree = new KDTree<>();
        final int dimensions = 3;
        final int size = 100;

        final String[][] keys = new String[size][dimensions];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < dimensions; j++) {
                keys[i][j] = 1000*Math.random() +"";
            }

            kdTree.add(keys[i], i + "");
        }


        while (kdTree.size() > 0) {
            final String[] minKey = kdTree.minKey();
            final String minValue = kdTree.min();
            final String[] maxKey = kdTree.maxKey();
            final String maxValue = kdTree.max();

            assertEquals(minValue, kdTree.removeMin());
            assertEquals(maxValue, kdTree.removeMax());
            assertNull(kdTree.find(minKey));
            assertNull(kdTree.find(maxKey));
        }

        assertNull(kdTree.minKey());
        assertNull(kdTree.min());

    }

    @Test
    public void testRandomAddRemoves() {
        final KDTree<String, String> kdTree = new KDTree<>();
        final int dimensions = 3;
        final int size = 100;

        final String[][] keys = new String[size][dimensions];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < dimensions; j++) {
                keys[i][j] = 1000*Math.random() +"";
            }

            kdTree.add(keys[i], i + "");
        }

        for (int i = 0; i < 100; i++) {
            final int idx = (int) (Math.random() * keys.length);
            final String[] key = keys[idx];

            if (kdTree.find(key) != null) {
                final int sz = kdTree.size();
                final String value = kdTree.find(key);
                assertEquals(value, kdTree.remove(key));
                assertEquals(sz-1, kdTree.size());
                assertNull(kdTree.find(key));
            }
        }
    }

    @Test
    public void testKeyPrefixFind() {
        final KDTree<Integer, String> kdTree = new KDTree<>();

        kdTree.add(new Integer[]{1,2,1}, "one");
        kdTree.add(new Integer[]{1,2,2}, "two");
        kdTree.add(new Integer[]{1,2,3}, "three");

        assertEquals("one", kdTree.find(new Integer[]{1,2,1}));
        assertEquals("two", kdTree.find(new Integer[]{1,2,2}));
        assertEquals("three", kdTree.find(new Integer[]{1,2,3}));

    }
}
