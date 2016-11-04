package com.github.basking2.sdsai.sexpr.functions;

import com.github.basking2.sdsai.sexpr.Evaluator;
import com.github.basking2.sdsai.sexpr.SExprRuntimeException;
import com.github.basking2.sdsai.sexpr.util.IteratorIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This function takes a list of function name and arguments and produces a function that can be applied to future objects.
 */
public class CurryFunction implements FunctionInterface<FunctionInterface<Object>> {

    private Evaluator evaluator;

    public CurryFunction(final Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public FunctionInterface<Object> apply(final Iterator<Object> args) {

        if (!args.hasNext()) {
            throw new SExprRuntimeException("CurryFunction requires at least 1 argument.");
        }

        final FunctionInterface<? extends Object> function = evaluator.getFunction(args.next());

        // Capture all evaluated args to a list.
        final List<Object> args2 = new ArrayList<>();
        while (args.hasNext()) {
            args2.add(args.next());
        }

        // Now return a new function that...
        return new FunctionInterface<Object>() {
            @Override
            public Object apply(Iterator<Object> args3) {
                return function.apply(new IteratorIterator(args2.iterator(), args3));
            }
        };
    }
}
