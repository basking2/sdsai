package com.github.basking2.sdsai.sexpr.functions;

import static com.github.basking2.sdsai.sexpr.util.Iterators.toIterator;

import java.util.Iterator;

import com.github.basking2.sdsai.sexpr.SExprRuntimeException;

/**
 * This defines most of a function that takes a list of Ts, iterators of Ts or iterables over Ts
 * and transforms them into an R.
 *
 * @param <T>
 */
public abstract class AbstractAggregatingFunction<T, R> implements FunctionInterface <R> {
    
    public final R initialValue;
    
    public AbstractAggregatingFunction(final R initialValue) {
        this.initialValue = initialValue;
    }

    @Override
    public R apply(final Iterator<?> iterator) {
        
        R r = initialValue;

        while (iterator.hasNext()) {
            final Object o = iterator.next();

            final Iterator<T> tList = toIterator(o);

            if (tList != null) {
                while (tList.hasNext()) {
                    r = applyT(r, tList.next());
                }
            } 
            else {
                try {
                    @SuppressWarnings("unchecked")
                    final T t = (T)o;
                    r = applyT(r, t);
                }
                catch (final ClassCastException e) {
                    throw new SExprRuntimeException(e.getMessage(), e);
                }
            }
        }

        return r;
    }
    
    public abstract R applyT(R r, T t);
}
