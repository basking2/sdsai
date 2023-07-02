/**
 * Copyright (c) 2023 Sam Baskinger
 */
package com.github.basking2.sdsai.itrex.iterators;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 */
public class PrefetchingIteratorTest {

    @Test
    public void testPrefetching() {
        assertThrows(NoSuchElementException.class, () -> {

            final ExecutorService es = Executors.newFixedThreadPool(3);

            final ArrayList<Integer> arr = new ArrayList<>();
            for (int i = 0; i < 100; ++i) {
                arr.add(i);
            }

            final Iterator<Integer> i = new PrefetchingIterator<Integer>(es, 10, arr.iterator());

            int prev = -1;
            while (i.hasNext()) {
                assertTrue(prev < i.next());
            }

            i.next();
        });
    }
}
