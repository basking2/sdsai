package com.github.basking2.sdsai.itrex.iterators;

import org.hamcrest.Matcher;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static com.github.basking2.sdsai.itrex.iterators.Iterators.mapIterator;

public class ParallelMappingIteratorTest {
    @Test
    public void testMapping() {
        final ExecutorService es = Executors.newFixedThreadPool(10);
        final Iterator<Integer> itr = new ParallelMappingIterator<Integer, Integer>(
                false,
                new RangeIterator(0,10, 1),
                es,
                10,
                (i) -> {
                    Thread.sleep((int)(Math.random()*100));
                    return i+1;
                }
                );

        // NOTE - we bump the range up from 0-10 to 1-11 to accommodate the mapping function above.
        final Integer[] expectedList = Iterators.toList(
            new RangeIterator(1,11, 1)).toArray(new Integer[0]);

        final List<Integer> actualList = Iterators.toList(itr);

        assertThat(actualList, hasItems(expectedList));
    }
}
