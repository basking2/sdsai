package com.github.basking2.sdsai.itrex.iterators;

import org.junit.Test;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test {@link Iterators#splitMapJoinIterator(ExecutorService, Iterator, Function, MappingIterator.Mapper)}.
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
                    new RangeIterator(0,10, 1)).toArray(new Integer[0]);

            assertThat(actualList, hasItems(expectedList));
            return ;
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
                    new RangeIterator(0,10, 1)).toArray(new Integer[0]);

            assertThat(actualList, hasItems(expectedList));
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
                new RangeIterator(0,10, 1)).toArray(new Integer[0]);

        es.shutdown();

        assertThat(actualList, hasItems(expectedList));
    }
}
