/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.EvaluationContext;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static com.github.basking2.sdsai.itrex.iterators.Iterators.mapIterator;

public class PrintArgsFunction implements FunctionInterface<Iterator<?>> {
    final private PrintStream out;

    public PrintArgsFunction(PrintStream out) {
        this.out = out;
    };

    @Override
    public Iterator<?> apply(final Iterator<?> iterator, final EvaluationContext evaluationContext) {

        final List<Object> argList = new LinkedList<>();

        iterator.forEachRemaining(arg -> {
            if (arg == null) {
                out.println("null");
            }
            else {
                out.println(arg + ":" + arg.getClass());
            }

            argList.add(arg);
        });

        return argList.iterator();
    }
}
