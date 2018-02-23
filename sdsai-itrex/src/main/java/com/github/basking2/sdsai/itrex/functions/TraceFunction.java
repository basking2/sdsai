package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.EvaluationContext;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A debugging function that prints to stdout trace data.
 */
public class TraceFunction implements FunctionInterface<Object> {

    final PrintStream out;

    public TraceFunction(final PrintStream out) {
        this.out = out;
    }

    public TraceFunction() {
        this.out = System.out;
    }

    @Override
    public Object apply(final Iterator<?> iterator, final EvaluationContext evaluationContext) {
        if (iterator.hasNext()) {
            final Object functionObject = iterator.next();
            out.print("[ ");
            if (functionObject instanceof FunctionInterface) {
                out.print(functionObject.toString());
            }
            else {
                out.print(functionObject.toString());
            }

            final FunctionInterface<? extends Object> function = evaluationContext.getFunction(functionObject);
            final List<Object> args = new ArrayList<>();

            while (iterator.hasNext()) {
                final Object arg = iterator.next();
                args.add(arg);
                out.print(" ");
                out.print(arg.toString());
            }
            out.print(" ]\n");

            return function.apply(args.iterator(), evaluationContext);
        }
        else {
            return null;
        }
    }
}
