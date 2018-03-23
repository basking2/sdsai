package com.github.basking2.sdsai;

/**
 * A heap that allows the compare functions to be specified.
 *
 * @param <T>
 */
public abstract class AbstractHeap<T> {

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

    private void halveQueue() {
        // ,"Queue should never shrink below 1.";
        assert queue.length > 1;

        final T[] newQueue = (T[]) new Object[queue.length / 2];
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
     * function would return true only when t1 > t2, meaning they should be re-sorted.
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

    public T remove() {
        if (last >= 0) {
            final T t = queue[0];

            swap(0, last);

            queue[last] = null;

            last -= 1;

            if (queue.length > 1 && last < queue.length/2) {
                halveQueue();
            }

            heapDown(0);

            return t;
        }
        else {
            return null;
        }
    }

    public int size() {
        return last+1;
    }

    public T get(final int i) {
        return queue[i];
    }
}
