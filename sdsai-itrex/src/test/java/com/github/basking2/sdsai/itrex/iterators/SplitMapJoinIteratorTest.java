/**
 * Copyright (c) 2023 Sam Baskinger
 */
package com.github.basking2.sdsai.itrex.iterators;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test {@link Iterators#splitMapJoinIterator(Executor, Iterator, Function, MappingIterator.Mapper)}.
 */
public class SplitMapJoinIteratorTest {
    @Test
    public void testSplitMapJoinSingleThread() throws ExecutionException, InterruptedException {

        final ExecutorService es = Executors.newFixedThreadPool(1);

        final Future<?> f = es.submit(() -> {
            final List<Integer> actualList = Iterators.toList(Iterators.splitMapJoinIterator(
                    es,
                    new RangeIterator(0, 10, 1),
                    (i) -> i % 3,
                    (i) -> i
            ));

            final Integer[] expectedList = Iterators.toList(
                    new RangeIterator(0, 10, 1)).toArray(new Integer[0]);

            for (final Integer e : expectedList) {
                assertTrue(actualList.contains(e));
            }
            return;
        });

        f.get();

        es.shutdown();
    }

    @Test
    public void testSplitMapJoinTwoThreads() throws ExecutionException, InterruptedException {
        final ExecutorService es = Executors.newFixedThreadPool(2);

        final Future<?> f = es.submit(() -> {
            final List<Integer> actualList = Iterators.toList(Iterators.splitMapJoinIterator(
                    es,
                    new RangeIterator(0, 10, 1),
                    (i) -> i % 3,
                    (i) -> i
            ));

            final Integer[] expectedList = Iterators.toList(
                    new RangeIterator(0, 10, 1)).toArray(new Integer[0]);

            for (final Integer e : expectedList) {
                assertTrue(actualList.contains(e));
            }
            return;
        });

        f.get();

        es.shutdown();

    }

    @Test
    public void testSplitMapJoinTwoThreadsAndMain() {
        final ExecutorService es = Executors.newFixedThreadPool(2);

        final List<Integer> actualList = Iterators.toList(Iterators.splitMapJoinIterator(
                es,
                new RangeIterator(0, 10, 1),
                (i) -> i % 3,
                (i) -> i
        ));

        final Integer[] expectedList = Iterators.toList(
                new RangeIterator(0, 10, 1)).toArray(new Integer[0]);

        es.shutdown();

        for (final Integer e : expectedList) {
            assertTrue(actualList.contains(e));
        }
    }
}
