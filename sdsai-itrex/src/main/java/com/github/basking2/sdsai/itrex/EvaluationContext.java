package com.github.basking2.sdsai.itrex;

import com.github.basking2.sdsai.itrex.functions.FunctionInterface;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class EvaluationContext {
    private EvaluationContext parent;
    private Map<Object, FunctionInterface<? extends Object>> functionRegistry = new HashMap<>();
    private Map<Object, Object> env = new HashMap<>();

    protected EvaluationContext()
    {
        this(null);
    }

    public EvaluationContext(final EvaluationContext parent)
    {
        this.parent = parent;
    }

    public void set(final Object key, final Object value) {
        env.put(key, value);
    }

    public Object get(final Object key) {
        for (EvaluationContext ec = this; ec != null; ec = ec.parent) {
            if (env.containsKey(key)) {
                return env.get(key);
            }
        }

        return null;
    }

    public boolean containsKey(final Object key) {
        for (EvaluationContext ec = this; ec != null; ec = ec.parent) {
            if (env.containsKey(key)) {
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
}
