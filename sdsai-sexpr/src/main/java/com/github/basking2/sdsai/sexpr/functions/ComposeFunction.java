package com.github.basking2.sdsai.sexpr.functions;

import com.github.basking2.sdsai.sexpr.util.Iterators;

import java.util.Iterator;
import java.util.function.Function;

/**
 * Given a list of functions, f, g, h, compose them such that they are called in
 * the order f(g(h)).
 */
public class ComposeFunction implements FunctionInterface<FunctionInterface<Object>> {

    @Override
    @SuppressWarnings("unchecked")
    public FunctionInterface<Object> apply(Iterator<?> iterator) {

        FunctionInterface<Object> f = null;

        while (iterator.hasNext()) {
            final Object o = iterator.next();
            if (o instanceof FunctionInterface<?>) {
                if (f == null) {
                    f = (FunctionInterface<Object>) o;
                }
                else {

                    final FunctionInterface<? extends Object> finalF = f;
                    final FunctionInterface<? extends Object> finalO = (FunctionInterface<? extends Object>) o;

                    f = in ->  {
                        final Object fOutput = Iterators.wrap(finalO.apply(in));
                        return finalF.apply((Iterator<? super Object>)fOutput);
                    };
                }
            }

        }

        return f;
    }
}
