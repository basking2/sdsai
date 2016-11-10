package com.github.basking2.sdsai.sexpr.functions;

import java.util.function.BiFunction;

/**
 * A collection of utilities for functions.
 */
public class Functions {
    
    public static <T, R> AbstractAggregatingFunction<T, R> aggregator(
        final R initial,
        final BiFunction<R, T, R> f
    ) {
        return new AbstractAggregatingFunction<T, R>(initial) {

            @Override
            public R applyT(R r, T t) {
                return f.apply(r, t);
            }

        };
    }

}
