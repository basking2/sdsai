/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.util.TypeConversion;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * The CaseFunction takes two arguments and returns two arguments.
 *
 * CaseFunction takes, first, an expression that yields a boolean.
 * Second it takes an expression that produces a result.
 *
 * If the first expression is true, then CaseFunction returns a list of first the true boolean and
 * then the result of the second expression's evaluation.
 *
 * If the first expression is false, then CaseFunction returns a list of first the false boolean and
 * then a null because the second expression will never be fetched, causing evaluation.
 */
public class CaseFunction implements FunctionInterface<List<Object>> {

    @Override
    public List<Object> apply(final Iterator<?> iterator, final EvaluationContext evaluationContext) {
        if (TypeConversion.toBoolean(iterator.next())) {
            return Arrays.asList(Boolean.TRUE, iterator.next());
        }
        else {
            return Arrays.asList(Boolean.FALSE, null);
        }
    }
}
