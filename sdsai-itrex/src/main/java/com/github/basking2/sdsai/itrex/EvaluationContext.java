package com.github.basking2.sdsai.itrex;

import com.github.basking2.sdsai.itrex.functions.FunctionInterface;
import com.github.basking2.sdsai.itrex.iterators.Iterators;

import java.util.*;

/**
 */
public class EvaluationContext {
    private EvaluationContext parent;
    private Map<Object, FunctionInterface<? extends Object>> functionRegistry;
    private Map<Object, Object> env;

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
        this.functionRegistry = new HashMap<>();
        this.env = new HashMap<>();
    }

    public EvaluationContext(
            final Map<Object, Object> env,
            final Map<Object, FunctionInterface<? extends Object> > functionRegistry,
            final Iterator<?> arguments,
            final EvaluationContext parent
    ) {
        this.env = env;
        this.functionRegistry = functionRegistry;
        this.arguments = arguments;
        this.parent = parent;
    }

    public static EvaluationContext functionCall(final EvaluationContext ctx, final Iterator<?> arguments)
    {
        return new EvaluationContext(
                ctx.env,
                ctx.functionRegistry,
                arguments,
                ctx
        );
    }

    public void set(final Object key, final Object value) {
        env.put(key, value);
    }

    /**
     * This is like set, but updates an already existing variable in the scope it is defined in.
     *
     * A call to set will merely set the variable in the current context, and that value will be lost on exit.
     *
     * This is a way to create side-effects, a very not-function practice, but sometimes necessary.
     *
     * If the value is not found to update an exception is thrown
     *
     * @param key The key to set.
     * @param value The value to associate with the key.
     */
    public void update(final Object key, final Object value) {
        for (EvaluationContext ec = this; ec != null; ec = ec.parent) {
            if (ec.env.containsKey(key)) {
                ec.env.put(key, value);
                return;
            }
        }

        throw new IllegalArgumentException("Attempt to update "+key+" when it is not yet defined.");
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

    /**
     * Get all functions from this context and all parent contexts.
     *
     * @return A set of all function identifiers that may be individually passed to {@link #getFunction(Object)}.
     */
    public Set<Object> getAllFunctions() {
        final Set<Object> functions = new HashSet<>();

        for (EvaluationContext ec = this; ec != null; ec = ec.parent) {
            functions.addAll(ec.functionRegistry.keySet());
        }

        return functions;
    }
}
