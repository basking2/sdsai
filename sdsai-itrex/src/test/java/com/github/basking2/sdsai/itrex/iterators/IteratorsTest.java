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
}
