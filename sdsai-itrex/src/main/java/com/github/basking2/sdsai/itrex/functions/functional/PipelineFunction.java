package com.github.basking2.sdsai.itrex.functions.functional;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.Evaluator;
import com.github.basking2.sdsai.itrex.SExprRuntimeException;
import com.github.basking2.sdsai.itrex.functions.FunctionInterface;
import static com.github.basking2.sdsai.itrex.iterators.Iterators.wrap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * A function that returns a function that ties many functions together.
 *
 * For readability any strings that are "|" or "=&lt;" are ignored.
 */
public class PipelineFunction implements FunctionInterface<FunctionInterface<Object>> {

    @Override
    public FunctionInterface<Object> apply(final Iterator<?> iterator, final EvaluationContext evaluationContext) {
        final List<FunctionInterface<?>> pipeline = new ArrayList<>();

        while (iterator.hasNext()) {
            final Object o = iterator.next();
            if (o instanceof FunctionInterface<?>) {
                @SuppressWarnings("unchecked")
                final FunctionInterface<?> f = (FunctionInterface<Object>)o;
                pipeline.add(f);
            }
            else if ("=>".equals(o) || "|".equals(o)) {
                // Nop.
            }
            else {
                final FunctionInterface<?> f = evaluationContext.getFunction(o);

                if (f == null) {
                    throw new SExprRuntimeException("No such function: "+o);
                }

                pipeline.add(f);
            }
        }

        return new FunctionInterface<Object>() {

            @Override
            public Object apply(final Iterator<?> iterator, final EvaluationContext evaluationContext) {
                Object result = null;
                Iterator<Object> args = (Iterator<Object>)iterator;
                for (final FunctionInterface<?> f : pipeline) {
                    result = f.apply(args, evaluationContext);
                    args = wrap(result);
                }

                return result;
            }
        };
    }
}
