package com.github.basking2.sdsai;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class HeapTest {
    @Test
    public void minHeapTest() {
        final AbstractHeap<Integer> heap = new AbstractHeap<Integer>() {
            @Override
            protected boolean unordered(final Integer t1, final Integer t2) {
                return t1 > t2;
            }
        };

        for (int i = 0; i < 100; ++i) {
            heap.add((int)(Math.random() * 100));
        }

        Integer prev = heap.remove();
        while (heap.size() > 0) {
            final Integer next = heap.remove();
            assertTrue(prev <= next);
            prev = next;
        }

    }
}
