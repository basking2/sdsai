package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.Evaluator;
import com.github.basking2.sdsai.itrex.SExprRuntimeException;
import com.github.basking2.sdsai.itrex.iterators.EvaluatingIterator;

import java.util.Iterator;

import static com.github.basking2.sdsai.itrex.iterators.Iterators.toIterator;

/**
 * Takes a variable name, iterator, and an expression that is unevaluated.
 *
 * This iterates over the values, setting the value specified by the name.
 *
 * Like {@code if} this should not be directly curried or composed.
 */
public class ForFunction implements FunctionInterface<Object> {

    private Evaluator evaluator;

    public ForFunction(final Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public Object apply(final Iterator<?> iterator, final EvaluationContext evaluationContext) {

        if (!(iterator instanceof EvaluatingIterator)) {
            throw new SExprRuntimeException("For cannot be curried, composed or similar as it requires access to the evaluation iterator.");
        }

        return applyImpl((EvaluatingIterator<?>)iterator, evaluationContext);
    }

    private Object applyImpl(final EvaluatingIterator<?> iterator, final EvaluationContext evaluationContext) {
        if (!iterator.hasNext()) {
            throw new SExprRuntimeException("For requires 3 arguments, a name, an iterable, and a body.");
        }

        final String name = iterator.next().toString();

        if (!iterator.hasNext()) {
            throw new SExprRuntimeException("For requires 3 arguments, a name, an iterable, and a body.");
        }

        final Iterator<Object> sequence = toIterator(iterator.next());

        if (sequence == null) {
            throw new SExprRuntimeException("Argument 2 to for is not convertable to an iterator.");
        }

        if (!iterator.hasNext()) {
            throw new SExprRuntimeException("For requires 3 arguments, a name, an iterable, and a body.");
        }

        iterator.setEvaluationEnabled(false);

        final Object expression = iterator.next();

        return forImpl(name, sequence, expression, evaluationContext);
    }

    private Object forImpl(final String name, final Iterator<Object> sequence, final Object body, final EvaluationContext context) {
        Object last = null;

        while (sequence.hasNext()) {
            context.set(name, sequence.next());
            last = evaluator.evaluate(body, context);
        }

        return last;
    }
}
