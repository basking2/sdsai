package com.github.basking2.sdsai;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

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

    @Test
    public void iteratorTest() {
        final AbstractHeap<Integer> heap = new AbstractHeap<Integer>() {
            @Override
            protected boolean unordered(final Integer t1, final Integer t2) {
                return t1 > t2;
            }
        };

        for (int i = 0; i < 100; ++i) {
            heap.add((int)(Math.random() * 100));
        }

        int count = 0;

        for (final int i : heap) {
            count++;
        }

        assertEquals(heap.size(), count);
    }

    @Test
    public void replaceTest() {
        final AbstractHeap<Integer> heap = new AbstractHeap<Integer>() {
            @Override
            protected boolean unordered(final Integer t1, final Integer t2) {
                return t1 > t2;
            }
        };

        heap.add(1);
        heap.add(2);
        heap.add(3);
        heap.add(4);
        heap.add(5);
        heap.add(6);
        heap.add(7);
        heap.add(8);

        heap.replace(0, 10);
        assertEquals(Integer.valueOf(2), heap.get(0));

        heap.replace(7, 3);
        assertEquals(Integer.valueOf(2), heap.get(0));
        assertEquals(Integer.valueOf(3), heap.get(1));
        assertEquals(Integer.valueOf(3), heap.get(2));
    }

    @Test
    public void randomReplaceTest() {
        final AbstractHeap<Integer> heap = new AbstractHeap<Integer>() {
            @Override
            protected boolean unordered(final Integer t1, final Integer t2) {
                return t1 > t2;
            }
        };

        final int N = 100;
        for (int i = 0; i < N; ++i){
            heap.add((int)Math.random() * N);
        }

        for (int i = 0; i < N; ++i){
            final int v = (int) Math.random() * N;
            heap.replace(i, v);
        }

        for (int i = 0; i < N; ++i){
            final int v = (int) Math.random() * N;
            final int j = (int) Math.random() * heap.size();
            heap.replace(j, v);
        }

    }
}
