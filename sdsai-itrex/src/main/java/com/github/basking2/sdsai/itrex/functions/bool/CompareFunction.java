/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.itrex.functions.bool;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.SExprRuntimeException;
import com.github.basking2.sdsai.itrex.functions.FunctionInterface;

import java.util.Iterator;

/**
 * The compare function is used to constuct [eq], [gt], [gte], [lt] and [lte].
 */
public class CompareFunction implements FunctionInterface<Boolean> {

    public static enum OP {
        EQ {
            @Override
            public boolean test(int i) {
                return i == 0;
            }
        },
        LT {
            @Override
            public boolean test(int i) {
                return i < 0;
            }
        },
        GT {
            @Override
            public boolean test(int i) {
                return i > 0;
            }
        },
        LTE {
            @Override
            public boolean test(int i) {
                return i <= 0;
            }
        },
        GTE {
            @Override
            public boolean test(int i) {
                return i >= 0;
            }
        };

        public abstract boolean test(int i);
    }

    private final OP operator;

    public CompareFunction(final OP operator) {
        this.operator = operator;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Boolean apply(Iterator<?> iterator, EvaluationContext evaluationContext) {

        if (iterator.hasNext()) {
            final Object o = iterator.next();

            if ( ! (o instanceof Comparable)) {
                throw new SExprRuntimeException("Arguments to compare functions must be Comparable objects.");
            }

            Comparable<Object> prev = (Comparable<Object>)o;

            while (iterator.hasNext()) {
                final Object current = iterator.next();
                if (current instanceof Comparable) {
                    int i = prev.compareTo(current);
                    if (!operator.test(i)) {
                        return false;
                    }

                    prev = (Comparable<Object>)current;
                }
                else {
                    return false;
                }
            }
        }

        return true;
    }
}
