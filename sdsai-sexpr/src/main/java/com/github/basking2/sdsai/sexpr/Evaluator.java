package com.github.basking2.sdsai.sexpr;

import static com.github.basking2.sdsai.sexpr.util.Iterators.toIterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.github.basking2.sdsai.sexpr.functions.*;
import com.github.basking2.sdsai.sexpr.util.EvaluatingIterator;
import com.github.basking2.sdsai.sexpr.util.Iterators;

/**
 */
public class Evaluator {

    private Map<Object, FunctionInterface<? extends Object>> functionRegistry;

    public Evaluator() {
        functionRegistry = new HashMap<>();

        register("help", new HelpFunction(this));
        register("version", new VersionFunction());
        register("curry", new CurryFunction(this));
        register("compose", new ComposeFunction());
        register("map", new MapFunction());
        register("last", new LastFunction());
        register("flatten", new FlattenFunction());
        register("list", new ListFunction());
        register("listFlatten", new ListFlattenFunction());
        register("print", new PrintArgsFunction(System.out));
        register("if", new IfFunction());
        register("let", new LetFunction());
        register("get", new GetFunction());
        register("set", new SetFunction());
        register("head", (iterator, ctx) -> toIterator(iterator.next()).next());
        register("tail", (iterator, ctx) -> {
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

    /**
     * Create a default, empty, evaluation context and call {@link #evaluate(Object, EvaluationContext)}.
     *
     * @param o The object to evaluate.
     * @return The result of the evaluation.
     */
    public Object evaluate(final Object o) {
        return evaluate(o, new EvaluationContext());
    }

    @SuppressWarnings("unchecked")
    public Object evaluate(final Object o, final EvaluationContext context) {
        if (o instanceof EvaluatingIterator) {
            return evaluate((EvaluatingIterator<Object>) o);
        }

        if (o instanceof Iterator) {
            return evaluate(wrap((Iterator<Object>) o, context));
        }

        if (o instanceof Iterable) {
            return evaluate(wrap(((Iterable<Object>) o).iterator(), context));
        }

        if (o instanceof Object[]) {
            return evaluate(wrap(Iterators.wrap((Object[])o), context));
        }

        return o;
    }

    public Object evaluate(final EvaluatingIterator<Object> i) {
        if (!i.hasNext()) {
            return new ArrayList<Object>().iterator();
        }

        final Object operatorObject = i.next();
        final FunctionInterface<? extends Object> operator;

        if (operatorObject instanceof FunctionInterface) {
            operator = (FunctionInterface<? extends Object>)operatorObject;
        }
        else if (functionRegistry.containsKey(operatorObject)) {
            operator = functionRegistry.get(operatorObject);
        }
        else {
            throw new SExprRuntimeException("No function "+operatorObject.toString());
        }

        return operator.apply(i, i.getEvaluationContext());
    }

    private EvaluatingIterator<Object> wrap(final Iterator<Object> iterator, final EvaluationContext context) {
        return new EvaluatingIterator<>(this, context, iterator);
    }
}
