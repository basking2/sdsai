package com.github.basking2.sdsai.itrex.functions;

import static com.github.basking2.sdsai.itrex.iterators.Iterators.toIterator;

import java.util.Iterator;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.SExprRuntimeException;

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
        
        Result r = new Result(true, initialValue);

        while (r.cont && iterator.hasNext()) {
            final Object o = iterator.next();

            final Iterator<T> tList = toIterator(o);

            if (tList != null) {
                while (r.cont && tList.hasNext()) {
                    r = applyT(r.result, tList.next());
                }
            } 
            else {
                try {
                    @SuppressWarnings("unchecked")
                    final T t = (T)o;
                    r = applyT(r.result, t);
                }
                catch (final ClassCastException e) {
                    throw new SExprRuntimeException(e.getMessage(), e);
                }
            }
        }

        return r.result;
    }

    /**
     *
     * @param r Right value.
     * @param t Left value.
     * @return A two-tuple which, if the left value is true, aggregation will continue.
     */
    public abstract Result applyT(R r, T t);

    public class Result {
        public boolean cont;
        public R result;
        public Result(final boolean cont, final R result) {
            this.cont = cont;
            this.result = result;
        }
    }
}
