package com.github.basking2.sdsai.itrex.functions;

import static com.github.basking2.sdsai.itrex.iterators.Iterators.toIterator;

import java.util.Iterator;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.SExprRuntimeException;
import com.github.basking2.sdsai.itrex.util.TwoTuple;

/**
 * This defines most of a function that takes a list of Ts, iterators of Ts or iterables over Ts
 * and transforms them into an R.
 *
 * @param <T> The input parameter type.
 * @param <R> The return type.
 */
public abstract class AbstractAggregatingFunction<T, R> implements FunctionInterface <R> {
    
    public final R initialValue;

    /**
     * Construct an function that will start aggregating values with initialValue as the first R value.
     *
     * If an implementation is summing numbers, initialValue may be 0.
     * If an implementation is building a list, initialValue may be an empty list.
     *
     * @param initialValue An intial value.
     */
    public AbstractAggregatingFunction(final R initialValue) {
        this.initialValue = initialValue;
    }

    @Override
    public R apply(final Iterator<?> iterator, final EvaluationContext evaluationContext) {
        
        TwoTuple<Boolean, R> r = new TwoTuple<>(Boolean.TRUE, initialValue);

        while (r.l && iterator.hasNext()) {
            final Object o = iterator.next();

            final Iterator<T> tList = toIterator(o);

            if (tList != null) {
                while (r.l && tList.hasNext()) {
                    r = applyT(r.r, tList.next());
                }
            } 
            else {
                try {
                    @SuppressWarnings("unchecked")
                    final T t = (T)o;
                    r = applyT(r.r, t);
                }
                catch (final ClassCastException e) {
                    throw new SExprRuntimeException(e.getMessage(), e);
                }
            }
        }

        return r.r;
    }

    /**
     *
     * @param r Right value.
     * @param t Left value.
     * @return A two-tuple which, if the left value is true, aggregation will continue.
     */
    public abstract TwoTuple<Boolean, R> applyT(R r, T t);
}
