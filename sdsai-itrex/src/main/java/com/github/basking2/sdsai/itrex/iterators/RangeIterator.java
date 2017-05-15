package com.github.basking2.sdsai.itrex.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterate over a range of values.
 */
public class RangeIterator implements Iterator<Integer> {

    private int current;
    private final int start;
    private final int stop;
    private final int step;

    public RangeIterator(final int start, final int stop, final int step) {

        if (step == 0) {
            throw new IllegalArgumentException("Step may not be 0.");
        }

        this.current = start;
        this.start = start;
        this.stop = stop;
        this.step = step;
    }

    @Override
    public boolean hasNext() {
        if (step < 0) {
            return (current > stop);
        }
        else {
            return (current < stop);
        }
    }

    @Override
    public Integer next() {

        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        final int r = current;

        current += step;

        return r;
    }
}
