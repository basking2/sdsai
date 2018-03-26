package com.github.basking2.sdsai;

import java.util.Iterator;

/**
 * A heap that allows the compare functions to be specified.
 *
 * @param <T>
 */
public abstract class AbstractHeap<T> implements Iterable<T> {

    @SuppressWarnings("unchecked")
    private T[] queue = (T[]) new Object[1];

    private int last = -1;

    private void doubleQueue() {
        final T[] newQueue = (T[]) new Object[queue.length * 2];
        for (int i = 0; i < queue.length; ++i) {
            newQueue[i] = queue[i];
        }
        queue = newQueue;
    }

    private void quarterQueue() {
        // ,"Queue should never shrink below 1.";
        assert queue.length >= 4;

        final T[] newQueue = (T[]) new Object[queue.length / 4];
        for (int i = 0; i < newQueue.length; ++i) {
            newQueue[i] = queue[i];
        }
        queue = newQueue;
    }

    private void heapDown(final int start) {
        for (int i = start; i < last;) {
            final int j = i * 2 + 1;
            final int k = j + 1;

            // Which child, j or k, can be moved up and not break the heap order? Bigger may move up.
            if (k <= last && unordered(queue[j], queue[k])) {
                // k may move up.

                if (unordered(queue[i], queue[j]) || unordered(queue[i], queue[k])) {
                    // k must move up
                    swap(i, k);
                    i = k;
                } else {
                    i = last;
                }
            } else if (j <= last) {
                // j may move up.

                if (unordered(queue[i], queue[j]) || (k <= last && unordered(queue[i], queue[k]))) {
                    // j must move up.
                    swap(i, j);
                    i = j;
                } else {
                    i = last;
                }
            } else {
                i = last;
            }
        }
    }

    private void heapUp(final int start) {
        for (int i = start; i > 0; ) {

            final int j = (i - 1) / 2;

            if (unordered(queue[j], queue[i])) {
                swap(i, j);
            }

            i = j;
        }
    }

    /**
     * Are t1 and t2 ordered. True if they _not_, false otherwise.
     *
     * Implementors should extend this class and override this function to determine the order of the elements in
     * this heap. Of you want a min-heap, a heap where things are sorted least-to-greatest, then this
     * function would return true only when t1 &gt; t2, meaning they should be re-sorted.
     *
     * @param t1 The value that should come first in this order.
     * @param t2 The value that should come second in this order.
     * @return True if t1 and t2 are unordered.
     */
    protected abstract boolean unordered(T t1, T t2);

    private void swap(final int i, final int j) {
        final T tmp = queue[j];
        queue[j] = queue[i];
        queue[i] = tmp;
    }

    public void add(final T t) {

        last += 1;

        if (last >= queue.length) {
            doubleQueue();
        }

        queue[last] = t;

        heapUp(last);
    }

    public T remove(final int i) {
        if (last >= 0) {
            final T t = queue[i];

            swap(i, last);

            queue[last] = null;

            last -= 1;

            if (queue.length >= 4 && last < queue.length/4) {
                quarterQueue();
            }

            heapDown(i);

            return t;
        }
        else {
            return null;
        }
    }

    public T remove() {
        return remove(0);
    }

    public T replace(final T newT) {
        return replace(0, newT);
    }

    public T replace(final int i, final T newT) {
        final T oldT = queue[i];

        queue[i] = newT;

        if (unordered(newT, oldT)) {
            // If newT is unordered from oldT, then newT cannot be oldT's parent, and we must heap-down.
            heapDown(i);
        }
        else {
            // If newTi is ordered with oldT, then newT can be oldT's parent and we should try to heap-up.
            heapUp(i);
        }


        return oldT;
    }

    public int size() {
        return last+1;
    }

    public T get(final int i) {
        return queue[i];
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            private int i = -1;

            @Override
            public boolean hasNext() {
                return i < last;
            }

            @Override
            public T next() {
                return queue[++i];
            }
        };
    }
}
