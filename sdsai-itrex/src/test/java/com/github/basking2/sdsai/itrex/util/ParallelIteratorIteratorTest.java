package com.github.basking2.sdsai.itrex.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 */
public class ParallelIteratorIteratorTest {

    @Test(expected= NoSuchElementException.class)
    public void testIteratrs() {
        final ExecutorService es = Executors.newFixedThreadPool(3);

        final ArrayList<Integer> arr1 = new ArrayList<>();
        final ArrayList<Integer> arr2 = new ArrayList<>();
        final ArrayList<Integer> arr3 = new ArrayList<>();
        final ArrayList<Integer> arr4 = new ArrayList<>();
        final ArrayList<Integer> arr5 = new ArrayList<>();

        for (int i = 0; i < 100; ++i) {
            arr1.add(1000 + i);
            arr2.add(2000 + i);
            arr3.add(3000 + i);
            arr4.add(4000 + i);
            arr5.add(5000 + i);
        }

        final Iterator<Integer> i = new ParallelIteratorIterator<Integer>(
                es,
                10,
                asList(arr1.iterator(), arr2.iterator(), arr3.iterator(), arr4.iterator(), arr5.iterator()));

        final ArrayList<Integer> results = new ArrayList<>(100 * 5);

        while (i.hasNext()) {
            results.add(i.next());
        }

        assertEquals(500, results.size());

        assertFalse(i.hasNext());

        i.next();
    }
}
