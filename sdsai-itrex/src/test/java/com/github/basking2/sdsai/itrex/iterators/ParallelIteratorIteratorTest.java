/**
 * Copyright (c) 2023 Sam Baskinger
 */
package com.github.basking2.sdsai.itrex.iterators;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

/**
 */
public class ParallelIteratorIteratorTest {

    @Test
    public void testIteratrs() {
        assertThrows(NoSuchElementException.class, () -> {
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

            es.shutdown();

            i.next();
        });
    }

    /**
     * Test that under situations where an executor has only 1 thread, deadlock does not occur.
     */
    @Test
    public void testStealWork1() {
        // Use an executor that does work on this thread.
        final Executor e = new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        };

        // 2 iterators that sleep.
        final Iterator i1 = asList(1,2,3).iterator();
        final Iterator i2 = asList(4,5,6).iterator();

        final Iterator<Integer> i = new ParallelIteratorIterator(e, 1, asList(i1, i2));

        final Set<Integer> resultSet = new HashSet<>();

        for (int element = 0; i.hasNext();) {
            resultSet.add(i.next());
        }

        assertTrue(resultSet.contains(1));
        assertTrue(resultSet.contains(2));
        assertTrue(resultSet.contains(3));
        assertTrue(resultSet.contains(4));
        assertTrue(resultSet.contains(5));
        assertTrue(resultSet.contains(6));
    }

    /**
     * Test that under situations where an executor has only 1 thread, deadlock does not occur.
     */
    @Test
    public void testStealWork2() {
        // Use an executor that does work on this thread.
        final ExecutorService e = Executors.newSingleThreadExecutor();

        // 2 iterators that sleep.
        final Iterator i1 = Iterators.mapIterator(asList(1,2,3).iterator(), i -> {Thread.sleep(200); return i;} );
        final Iterator i2 = Iterators.mapIterator(asList(4,5,6).iterator(), i -> {Thread.sleep(200); return i;} );

        final Iterator<Integer> i = new ParallelIteratorIterator(e, 1, asList(i1, i2));

        final Set<Integer> resultSet = new HashSet<>();

        for (int element = 0; i.hasNext();) {
            resultSet.add(i.next());
        }

        assertTrue(resultSet.contains(1));
        assertTrue(resultSet.contains(2));
        assertTrue(resultSet.contains(3));
        assertTrue(resultSet.contains(4));
        assertTrue(resultSet.contains(5));
        assertTrue(resultSet.contains(6));

        e.shutdown();
    }

    @Test
    public void testEmptyIterator() {
        // Use an executor that does work on this thread.
        final ExecutorService e = Executors.newSingleThreadExecutor();

        final List<String> nullList = new ArrayList<>();
        nullList.add(null);
        nullList.add(null);
        nullList.add(null);

        final Iterator<String> nullIterator = Iterators.skipNulls(nullList.iterator());

        // 2 iterators that sleep.
        final Iterator<Integer> i = new ParallelIteratorIterator(e, 1, asList(nullIterator));

        assertFalse(i.hasNext());

        e.shutdown();
    }
}
