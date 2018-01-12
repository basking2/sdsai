package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.EvaluationContext;

import java.io.PrintStream;
import java.util.Iterator;

import static com.github.basking2.sdsai.itrex.iterators.Iterators.mapIterator;

public class TraceArgsFunction implements FunctionInterface<Iterator<?>> {
    final private PrintStream out;

    public TraceArgsFunction(PrintStream out) {
        this.out = out;
    };

    @Override
    public Iterator<?> apply(final Iterator<?> iterator, final EvaluationContext evaluationContext) {
        return mapIterator(iterator, arg -> {
            if (arg == null) {
                out.println("null");
            }
            else {
                out.println(arg + ":" + arg.getClass());
            }

            return arg;
        });
    }
}
