package com.github.basking2.sdsai.sexpr.util;

import com.github.basking2.sdsai.sexpr.Evaluator;

import java.util.Iterator;

public class EvaluatingIterator<T extends Object> implements Iterator<T> {

    private Iterator<Object> itr;
    private boolean evaluationEnabled;
    private Evaluator evaluator;

    public EvaluatingIterator(final Evaluator evaluator, final Iterator<Object> itr) {
        this.evaluator = evaluator;
        this.itr = itr;
        this.evaluationEnabled = true;
    }

    @Override
    public boolean hasNext() {
        return itr.hasNext();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T next() {
        return evaluationEnabled ? (T)evaluator.evaluate(itr.next()) : (T)itr.next();
    }

    /**
     * Set whether this iterator should map objects through its evaluator or not.
     *
     * @param evaluationEnabled
     */
    public void setEvaluationEnabled(final boolean evaluationEnabled) {
        this.evaluationEnabled = evaluationEnabled;
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
