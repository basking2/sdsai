package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.EvaluationContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.github.basking2.sdsai.itrex.util.Iterators.toIterator;

/**
 * Flatten arguments and iterators into a list.
 */
public class ListFlattenFunction implements HelpfulFunction, FunctionInterface<List<Object>> {

    @Override
    public List<Object> apply(final Iterator<?> iterator, final EvaluationContext evaluationContext) {

        final List<Object> list = new ArrayList<Object>();

        while (iterator.hasNext()) {
            final Object o = iterator.next();

            final Iterator<Object> i = toIterator(o);

            if (i == null) {
                list.add(i);
            }
            else {
                while (i.hasNext()) {
                    list.add(i.next());
                }
            }
        }

        return list;
    }

    @Override
    public String functionHelp(final String name, final boolean verbose) {
        final StringBuilder sb = new StringBuilder();

        if (verbose) {
            sb.append("## ").append(name).append("\n\n");
        }

        sb
                .append("listFlatten args and iterators\n\n")
                .append("Every argument not an iterator is added to a list.\n")
                .append("Every iterator that is an argument is drained into a list.\n")
                .append("This list is returned.")
                .append("This is a bit more tollerant than the flatten function in that non-list\n")
                .append("arguments will not cause a crash, but will just be added to the returned list.\n")
                .append("\n")
                ;

        return sb.toString();
    }
}
