package com.github.basking2.sdsai.sexpr;

import java.util.*;
import java.util.function.Function;

/**
 */
public class Evaluator {

    private Map<Object, Function<EvaluatingIterator, Object>> functionRegistry;

    public Evaluator() {
        functionRegistry = new HashMap<>();
    }

    public void register(final Object name, final Function<EvaluatingIterator, Object> operator) {
        functionRegistry.put(name, operator);
    }

    public Object evaluate(final Object o) {
        if (o instanceof EvaluatingIterator) {
            return evaluate((EvaluatingIterator) o);
        }

        if (o instanceof Iterator) {
            return evaluate(wrap((Iterator) o));
        }

        if (o instanceof Iterable) {
            return evaluate(wrap(((Iterable) o).iterator()));
        }

        if (o instanceof List) {
            return evaluate(wrap(((List<Object>) o).iterator()));
        }

        if (o instanceof Object[]) {
            return evaluate(wrap(Arrays.asList((Object[])o).iterator()));
        }

        return o;
    }

    public Object evaluate(final EvaluatingIterator i) {
        if (!i.hasNext()) {
            return new ArrayList().iterator();
        }

        final Object operatorObject = i.next();
        final Function<EvaluatingIterator, Object> operator = functionRegistry.get(operatorObject);
        if (operator == null) {
            throw new MapAlgebraRuntimeException("No function "+operatorObject.toString());
        }

        return operator.apply(i);
    }

    private EvaluatingIterator wrap(final Iterator<Object> iterator) {
        return new EvaluatingIterator() {
            public boolean hasNext() {
                return iterator.hasNext();
            }

            public Object next() throws MapAlgebraRuntimeException {
                return evaluate(iterator.next());
            }
        };
    }

    public interface EvaluatingIterator extends Iterator<Object> {

        boolean hasNext();

        Object next();
    }
}
