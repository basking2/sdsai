package com.github.basking2.sdsai.sexpr;

/**
 */
public class EvaluationContext {
    private EvaluationContext parent;

    public EvaluationContext()
    {
        this(null);
    }

    public EvaluationContext(final EvaluationContext parent)
    {
        this.parent = parent;
    }
}
