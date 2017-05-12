package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.SExprRuntimeException;
import com.github.basking2.sdsai.itrex.iterators.EvaluatingIterator;

import java.util.Iterator;

/**
 */
public class LetFunction implements FunctionInterface<Object>, HelpfulFunction {

    @Override
    public Object apply(final Iterator<?> iterator, final EvaluationContext evaluationContext) {
        if (! (evaluationContext instanceof EvaluationContext)) {
            throw new SExprRuntimeException("Let argument iterator is not an evaluating iterator. Can't modify environment. Let may not be used in expressions like curry or compose as it proxies access to the evaluation iterator.");
        }

        // Build the new context with the same arguments.
        final EvaluationContext newContext = new EvaluationContext(evaluationContext, evaluationContext.getArguments());

        // Cast the iterator down so we can influence the context.
        @SuppressWarnings("unchecked")
        final EvaluatingIterator<Object> evaluatingIterator = (EvaluatingIterator<Object>)iterator;

        Object evaluatedObject = null;
        while (iterator.hasNext()) {
            // Evaluate the set block to modify the newContext.
            evaluatedObject = evaluatingIterator.next(newContext);
        }

        return evaluatedObject;
    }

    @Override
    public String functionHelp(final String name, final boolean verbose) {
        final StringBuilder sb = new StringBuilder();
        if (verbose) {
            sb.append("## ").append(name).append("\n\n");
        }
        sb.append("Set variables in a scoped context. This is thread safe.\n\n")
                .append("This operates by creating a new context and evaluating every argument. ")
                .append("The value of the last argument is returned.\n\n")
                .append("see set and get functions.\n\n")
                .append("    [ \"let\", [\"set\", \"a\", 1], [\"set\", \"b\", 2], [\"f\"]]")
                ;
        return sb.toString();
    }
}
