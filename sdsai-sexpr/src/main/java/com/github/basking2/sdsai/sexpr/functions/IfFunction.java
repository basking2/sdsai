package com.github.basking2.sdsai.sexpr.functions;

import java.util.Iterator;

import com.github.basking2.sdsai.sexpr.Evaluator;
import com.github.basking2.sdsai.sexpr.SExprRuntimeException;
import com.github.basking2.sdsai.sexpr.util.EvaluatingIterator;

public class IfFunction implements FunctionInterface<Object> {

    @Override
    public Object apply(Iterator<Object> args) {
        if (!(args.hasNext())) {
            throw new SExprRuntimeException("If requires 3 arguments.");
        }
        final Object predicate = args.next();
        
        if (!(args.hasNext())) {
            throw new SExprRuntimeException("If requires 3 arguments.");
        }
        
        if (
                (predicate instanceof Boolean && (Boolean)predicate) ||
                (predicate instanceof Integer && (Integer)predicate != 0) 
        ) {
            return args.next();
        }
        
        // Skip the true branch.
        if (args instanceof EvaluatingIterator) {
            ((EvaluatingIterator)args).skip();
        }
        else {
            args.next();
        }

        // Make sure we have the false branch.
        if (!(args.hasNext())) {
            throw new SExprRuntimeException("If requires 3 arguments.");
        }
        return args.next();
    }
}
