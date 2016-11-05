package com.github.basking2.sdsai.sexpr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.github.basking2.sdsai.sexpr.functions.CurryFunction;
import com.github.basking2.sdsai.sexpr.functions.FunctionInterface;
import com.github.basking2.sdsai.sexpr.functions.IfFunction;
import com.github.basking2.sdsai.sexpr.functions.LastFunction;
import com.github.basking2.sdsai.sexpr.functions.ListFunction;
import com.github.basking2.sdsai.sexpr.functions.MapFunction;
import com.github.basking2.sdsai.sexpr.util.Iterators;
import com.github.basking2.sdsai.sexpr.util.MappingIterator;

/**
 */
public class Evaluator {

    private Map<Object, FunctionInterface<? extends Object>> functionRegistry;

    public Evaluator() {
        functionRegistry = new HashMap<>();

        register("curry", new CurryFunction(this));
        register("map", new MapFunction());
        register("list", new ListFunction());
        register("last", new LastFunction());
        register("if", new IfFunction());
    }

    public void register(final Object name, final FunctionInterface<? extends Object> operator) {
        functionRegistry.put(name, operator);
    }

    public FunctionInterface<? extends Object> getFunction(final Object functionName) {
        return functionRegistry.get(functionName);
    }

    public Object evaluate(final Object o) {
        if (o instanceof EvaluatingIterator) {
            return evaluate((EvaluatingIterator) o);
        }

        if (o instanceof Iterator) {
            return evaluate(wrap((Iterator) o));
        }

        if (o instanceof Iterable) {
            return evaluate(wrap(((Iterable) o).iterator()));
        }

        if (o instanceof Object[]) {
            return evaluate(wrap(Iterators.wrap((Object[])o)));
        }

        return o;
    }

    public Object evaluate(final Iterator<Object> i) {
        if (!i.hasNext()) {
            return new ArrayList().iterator();
        }

        final Object operatorObject = i.next();
        final FunctionInterface<? extends Object> operator = functionRegistry.get(operatorObject);
        if (operator == null) {
            throw new SExprRuntimeException("No function "+operatorObject.toString());
        }

        return operator.apply(i);
    }

    private EvaluatingIterator wrap(final Iterator<Object> iterator) {
        return new EvaluatingIterator(this, iterator);
    }

    /**
     * An iterator that will evaluate every element before passing it back using the outer class' evaluator.
     */
    public static class EvaluatingIterator extends MappingIterator<Object, Object> {
        private Iterator<Object> itr;
        public EvaluatingIterator(final Evaluator evaluator, final Iterator<Object> itr) {
            super(itr, e -> evaluator.evaluate(e));
            this.itr = itr;
        }
        
        /**
         * Advance the iterator without evaluating it.
         * 
         * This is useful for conditional executions, such is ["if"].
         */
        public void skip() {
            this.itr.next();
        }
    }
}
