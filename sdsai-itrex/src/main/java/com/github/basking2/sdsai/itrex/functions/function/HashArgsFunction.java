/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.itrex.functions.function;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.functions.FunctionInterface;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Put all strings of the format name:value into the environment as name = value.
 *
 * This allows {@code [get name]} to return the {@code value}.
 *
 * Unconsumed arguments are returned in an iterator _and_ set as the current arguments in the environment.
 *
 * Subsequent calls to [arg] or [args] will get the filtered list of arguments.
 *
 */
public class HashArgsFunction implements FunctionInterface<Iterator<Object>>
{
    @Override
    public Iterator<Object> apply(final Iterator<?> iterator, final EvaluationContext evaluationContext) {
        final List<Object> unconsumedArgs = new ArrayList<>();

        while (iterator.hasNext()) {
            final Object o = iterator.next();
            if (o != null) {
                if (o instanceof String) {
                    final String[] argArray = ((String) o).split(":", 2);
                    if (argArray.length == 2) {
                        evaluationContext.set(argArray[0], argArray[1]);
                    }
                    else {
                        unconsumedArgs.add(o);
                    }
                }
                else {
                    unconsumedArgs.add(o);
                }
            }
        }

        final Iterator<Object> unconsumedArgsIterator = unconsumedArgs.iterator();

        evaluationContext.setArguments(unconsumedArgsIterator);

        return unconsumedArgsIterator;
    }
}
