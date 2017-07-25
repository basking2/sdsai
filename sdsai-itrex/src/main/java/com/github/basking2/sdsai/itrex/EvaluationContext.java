package com.github.basking2.sdsai.itrex;

import com.github.basking2.sdsai.itrex.functions.FunctionInterface;
import com.github.basking2.sdsai.itrex.iterators.Iterators;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 */
public class EvaluationContext {
    private EvaluationContext parent;
    private Map<Object, FunctionInterface<? extends Object>> functionRegistry = new HashMap<>();
    private Map<Object, Object> env = new HashMap<>();

    /**
     * To support function calls we must hold an arguments record.
     *
     * A function call does not require a sub-context, but one is recommended.
     * This prevents a calling function's argument from being lost by another function's arguments.
     */
    private Iterator<?> arguments;

    protected EvaluationContext()
    {
        this(null, Iterators.EMPTY_ITERATOR);
    }

    public EvaluationContext(final EvaluationContext parent, final Iterator<?> arguments)
    {
        this.parent = parent;
        this.arguments = arguments;
    }

    public void set(final Object key, final Object value) {
        env.put(key, value);
    }

    public Object get(final Object key) {
        for (EvaluationContext ec = this; ec != null; ec = ec.parent) {
            if (ec.env.containsKey(key)) {
                return ec.env.get(key);
            }
        }

        return null;
    }

    public boolean containsKey(final Object key) {
        for (EvaluationContext ec = this; ec != null; ec = ec.parent) {
            if (ec.env.containsKey(key)) {
                return true;
            }
        }

        return false;
    }

    public void register(final Object name, final FunctionInterface<? extends Object> operator) {
        functionRegistry.put(name, operator);
    }

    public FunctionInterface<? extends Object> getFunction(final Object functionName) {
        for (EvaluationContext ec = this; ec != null; ec = ec.parent) {
            if (ec.functionRegistry.containsKey(functionName)) {
                return ec.functionRegistry.get(functionName);
            }
        }

        return null;
    }

    public void setArguments(final Iterator<?> arguments) {
        this.arguments = arguments;
    }

    public Iterator<?> getArguments() {
        return arguments;
    }
}
