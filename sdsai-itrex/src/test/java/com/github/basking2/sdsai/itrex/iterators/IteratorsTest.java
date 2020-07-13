package com.github.basking2.sdsai.itrex.iterators;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class IteratorsTest {

    @Test
    public void testMergingLists() {

        final Iterator<Integer> l1 = Arrays.asList(1, 5, 8, 15, 20, 30).iterator();
        final Iterator<Integer> l2 = Arrays.asList(2, 4, 8, 16, 20).iterator();
        final ArrayList<Integer> l3 = new ArrayList();
        Iterators.mergeSorted(l1, v -> v, l2, v -> v, (v1, v2) -> {
            Assert.assertEquals(v1, v2);
            l3.add(v1);
        });

        Assert.assertEquals(l3.size(), 2);
        Assert.assertEquals(l3.get(0), Integer.valueOf(8));
        Assert.assertEquals(l3.get(1), Integer.valueOf(20));
    }

    @Test
    public void testMergingListsDescending() {

        final Iterator<Integer> l1 = Arrays.asList(30, 20, 15, 8, 5, 1).iterator();
        final Iterator<Integer> l2 = Arrays.asList(20, 16, 8, 4, 2).iterator();
        final ArrayList<Integer> l3 = new ArrayList();
        Iterators.mergeSortedDescending(l1, v -> v, l2, v -> v, (v1, v2) -> {
            Assert.assertEquals(v1, v2);
            l3.add(v1);
        });

        Assert.assertEquals(l3.size(), 2);
        Assert.assertEquals(l3.get(0), Integer.valueOf(20));
        Assert.assertEquals(l3.get(1), Integer.valueOf(8));
    }

    @Test
    public void testMergeOrHandleLists() {

        final Iterator<Integer> l1 = Arrays.asList(1, 5, 8, 15, 20, 30).iterator();
        final Iterator<Integer> l2 = Arrays.asList(2, 4, 8, 16, 20).iterator();
        final ArrayList<Integer> l3 = new ArrayList();
        Iterators.mergeOrHandleSorted(l1, v -> v, l2, v -> v, (v1, v2) -> {
            if (v1 != null) {
                l3.add(v1);
            } else {
                l3.add(v2);
            }
        });

        Assert.assertEquals(9, l3.size());
        Assert.assertEquals(Integer.valueOf(1), l3.get(0));
        Assert.assertEquals(Integer.valueOf(2), l3.get(1));
        Assert.assertEquals(Integer.valueOf(4), l3.get(2));
        Assert.assertEquals(Integer.valueOf(5), l3.get(3));
        Assert.assertEquals(Integer.valueOf(8), l3.get(4));
        Assert.assertEquals(Integer.valueOf(15), l3.get(5));
        Assert.assertEquals(Integer.valueOf(16), l3.get(6));
        Assert.assertEquals(Integer.valueOf(20), l3.get(7));
        Assert.assertEquals(Integer.valueOf(30), l3.get(8));
    }
}
