package com.github.basking2.sdsai.sexpr.functions;

import java.util.Iterator;

import com.github.basking2.sdsai.sexpr.EvaluationContext;
import com.github.basking2.sdsai.sexpr.Evaluator;

/**
 * A function that fetches function help text.
 */
public class HelpFunction implements FunctionInterface<String>, HelpfulFunction {
    
    private final Evaluator evaluator;
    
    /**
     * Constructor.
     * 
     * @param evaluator The evaluator that provides function definitions to get help text for.
     */
    public HelpFunction(final Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String apply(final Iterator<? extends Object> args, final EvaluationContext evaluationContext) {
        
        final StringBuilder stringBuilder = new StringBuilder();
        boolean verbose = false;

        while (args.hasNext()) {
            final Object o = args.next();
            HelpfulFunction hf = null;

            if (o instanceof HelpfulFunction) {
                hf = (HelpfulFunction)o;
            }
            else if (o instanceof String) {
                final String s = (String)o;
                if ("verbose".equals(s) || "-v".equals(s)) {
                    verbose = true;
                    continue;
                }
                else {
                    final FunctionInterface<?> f = evaluator.getFunction(s);
                    if (f instanceof HelpfulFunction) {
                        hf = (HelpfulFunction)o;
                    }
                }
            }

            if (hf != null) {
                stringBuilder.
                    append(hf.functionHelp(verbose)).
                    append("\n");
            }
            else {
                stringBuilder.append("No help for function ").append(o.toString()).append(".\n");
            }
        }

        return stringBuilder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String functionHelp(boolean verbose) {
        return "    [\"help\", [-v], [verbose], \"function name\", [\"curry\" \"another function name\"]]\n" +
               "      -v       - Turn on verbose output, if available.\n" +
               "      version  - Turn on verbose output, if available.\n" +
               "      string   - Strings are used as names of functions to fetch help for.\n"+
               "      function - If a function object is an argument, its help data is retrieved."
               ;
    }


}
