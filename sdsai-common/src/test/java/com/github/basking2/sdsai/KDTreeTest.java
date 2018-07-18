package com.github.basking2.sdsai;

import org.junit.Test;

import static org.junit.Assert.*;

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
