package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.iterators.Iterators;
import com.github.basking2.sdsai.itrex.iterators.RangeIterator;

import java.util.Iterator;

/**
 * Build a range function.
 */
public class RangeFunction implements FunctionInterface<Iterator<Integer>> {

    @Override
    public Iterator<Integer> apply(final Iterator<?> iterator, final EvaluationContext evaluationContext) {

        if (!iterator.hasNext()) {
            @SuppressWarnings("unchecked")
            final Iterator<Integer> emptyIterator = (Iterator<Integer>) Iterators.EMPTY_ITERATOR;
            return emptyIterator;
        }

        final int start = Integer.valueOf(iterator.next().toString());
        if (!iterator.hasNext()) {
            // When there is no 2nd argument, start is used as the stop value.
            // Start is 0 and step is 1.
            return new RangeIterator(0, start, 1);
        }

        final int stop = Integer.valueOf(iterator.next().toString());
        if (!iterator.hasNext()) {
            return new RangeIterator(start, stop, 1);
        }

        final int step = Integer.valueOf(iterator.next().toString());

        return new RangeIterator(start, stop, step);
    }
}
