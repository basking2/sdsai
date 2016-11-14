package com.github.basking2.sdsai.sexpr;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class EvaluationContext {
    private EvaluationContext parent;
    private Map<Object, Object> env = new HashMap<>();

    public EvaluationContext()
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
}
