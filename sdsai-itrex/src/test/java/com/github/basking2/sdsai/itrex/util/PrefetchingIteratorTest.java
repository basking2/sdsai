package com.github.basking2.sdsai.itrex.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 */
public class PrefetchingIteratorTest {

    @Test(expected= NoSuchElementException.class)
    public void testPrefetching() {
        final ExecutorService es = Executors.newFixedThreadPool(3);

        final ArrayList<Integer> arr = new ArrayList<>();
        for (int i = 0; i < 100; ++i) {
            arr.add(i);
        }

        final Iterator<Integer> i = new PrefetchingIterator<Integer>(es, arr.iterator(), 10);

        int prev = -1;
        while (i.hasNext()) {
            Assert.assertTrue(prev < i.next());
        }

        i.next();
    }
}
