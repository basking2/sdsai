/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.itrex.functions.functional;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.SExprRuntimeException;
import com.github.basking2.sdsai.itrex.functions.FunctionInterface;
import com.github.basking2.sdsai.itrex.functions.HelpfulFunction;
import com.github.basking2.sdsai.itrex.iterators.IteratorIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * This function takes a list of function name and arguments and produces a function that can be applied to future objects.
 */
public class CurryFunction implements HelpfulFunction, FunctionInterface<FunctionInterface<Object>> {

    @Override
    public FunctionInterface<Object> apply(final Iterator<?> args, final EvaluationContext evaluationContext) {

        if (!args.hasNext()) {
            throw new SExprRuntimeException("CurryFunction requires at least 1 argument.");
        }

        final FunctionInterface<? extends Object> function = evaluationContext.getFunction(args.next());

        // Capture all evaluated args to a list.
        final List<Object> args2 = new ArrayList<>();
        while (args.hasNext()) {
            args2.add(args.next());
        }

        // Now return a new function that...
        return new FunctionInterface<Object>() {
            @Override
            public Object apply(final Iterator<?> args3, final EvaluationContext evaluationContext) {
                @SuppressWarnings("unchecked")
                final Iterator<Object> args3Downcast = (Iterator<Object>)args3;
                return function.apply(new IteratorIterator<>(asList(args2.iterator(), args3Downcast)), evaluationContext);
            }
        };
    }

    @Override
    public String functionHelp(final String name, final boolean verbose) {
        final StringBuilder sb = new StringBuilder();
        
        if (verbose) {
            sb.append("## ").append(name).append("\n\n");
        }
        
        sb.append("Return a curried function names in the first argument to this function.\n\n");
        sb.append("This function takes as its first argument a function or a string that names a function.\n");
        sb.append("All subsequent argumets are evaluated and stored.\n");
        sb.append("When the function returned by \"curry\" is called it is passed ")
          .append("the evaluated arguments followed by the formal arguments. ");
        
        if (verbose) {
            sb.append("\nAnd example:\n\n")
              .append("    [\"map\", [\"curry\", \"add\", 3], 4, 5\n\n")
              .append("The above code will return an iteration containing 7, 8. ")
              .append("The function \"add\" is bound to the integer 3 and a function is returned. ")
              .append("When that function is returned with the argument 4, 3 is added to 4 and so on.");
            
        }

        return sb.toString();
    }
}
