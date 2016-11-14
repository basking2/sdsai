package com.github.basking2.sdsai.sexpr;

import static com.github.basking2.sdsai.sexpr.util.Iterators.toIterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.github.basking2.sdsai.sexpr.functions.ComposeFunction;
import com.github.basking2.sdsai.sexpr.functions.CurryFunction;
import com.github.basking2.sdsai.sexpr.functions.FlattenFunction;
import com.github.basking2.sdsai.sexpr.functions.FunctionInterface;
import com.github.basking2.sdsai.sexpr.functions.HelpFunction;
import com.github.basking2.sdsai.sexpr.functions.IfFunction;
import com.github.basking2.sdsai.sexpr.functions.LastFunction;
import com.github.basking2.sdsai.sexpr.functions.ListFunction;
import com.github.basking2.sdsai.sexpr.functions.MapFunction;
import com.github.basking2.sdsai.sexpr.functions.PrintArgsFunction;
import com.github.basking2.sdsai.sexpr.util.EvaluatingIterator;
import com.github.basking2.sdsai.sexpr.util.Iterators;

/**
 */
public class Evaluator {

    private Map<Object, FunctionInterface<? extends Object>> functionRegistry;

    public Evaluator() {
        functionRegistry = new HashMap<>();

        register("help", new HelpFunction(this));
        register("curry", new CurryFunction(this));
        register("map", new MapFunction());
        register("list", new ListFunction());
        register("last", new LastFunction());
        register("compose", new ComposeFunction());
        register("flatten", new FlattenFunction());
        register("print", new PrintArgsFunction(System.out));
        register("if", new IfFunction());
        register("head", iterator -> toIterator(iterator.next()).next());
        register("tail", iterator -> {
            Iterator<?> i = toIterator(iterator.next());
            i.next();
            return i;
        });
    }

    public void register(final Object name, final FunctionInterface<? extends Object> operator) {
        functionRegistry.put(name, operator);
    }

    public FunctionInterface<? extends Object> getFunction(final Object functionName) {
        return functionRegistry.get(functionName);
    }

    @SuppressWarnings("unchecked")
    public Object evaluate(final Object o) {
        if (o instanceof EvaluatingIterator) {
            return evaluate((EvaluatingIterator<Object>) o);
        }

        if (o instanceof Iterator) {
            return evaluate(wrap((Iterator<Object>) o));
        }

        if (o instanceof Iterable) {
            return evaluate(wrap(((Iterable<Object>) o).iterator()));
        }

        if (o instanceof Object[]) {
            return evaluate(wrap(Iterators.wrap((Object[])o)));
        }

        return o;
    }

    public Object evaluate(final Iterator<Object> i) {
        if (!i.hasNext()) {
            return new ArrayList<Object>().iterator();
        }

        final Object operatorObject = i.next();
        final FunctionInterface<? extends Object> operator = functionRegistry.get(operatorObject);
        if (operator == null) {
            throw new SExprRuntimeException("No function "+operatorObject.toString());
        }

        return operator.apply(i);
    }

    private EvaluatingIterator<Object> wrap(final Iterator<Object> iterator) {
        return new EvaluatingIterator<>(this, iterator);
    }
}
