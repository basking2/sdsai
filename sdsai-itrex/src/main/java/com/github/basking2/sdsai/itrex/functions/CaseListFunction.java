package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.iterators.Iterators;
import com.github.basking2.sdsai.itrex.util.TypeConversion;

import java.util.Iterator;

/**
 * The `caselist` function is built to work with `case` functions, but this is not necessary.
 *
 * Find will evaluate each of its arguments, in order, until it _finds_ a result.
 *
 * A result is _found_ if the argument either evaluates to _true_ or is an iterable object
 * and its first element evaluates to _true_. In the case of an iterable element, the second
 * element in the iterable is returned as the actual result. In the case of a non-iterable,
 * then just true is returned.
 *
 * If nothing is found, then null (not _false_) is returned.
 */
public class CaseListFunction implements FunctionInterface<Object> {
    @Override
    public Object apply(final Iterator<?> iterator, final EvaluationContext evaluationContext) {
        while (iterator.hasNext()) {
            final Object o = iterator.next();

            if (o == null) {
                continue;
            }

            final Iterator<Object> i = Iterators.toIterator(o);

            if (i == null) {
                if (TypeConversion.toBoolean(o)) {
                    return o;
                }
            }
            else {
                // If the iterator is not empty...
                if (i.hasNext()) {
                    // ... and the next value is a truthy value...
                    if (TypeConversion.toBoolean(i.next())) {
                        // ... and there is a result object in the iterator...
                        if (i.hasNext()) {
                            // ... return, we are done!
                            return i.next();
                        } else {
                            // If we found a true value, but there was no next element, return true.
                            return Boolean.TRUE;
                        }
                    }
                }
            }
        }

        return null;
    }
}
