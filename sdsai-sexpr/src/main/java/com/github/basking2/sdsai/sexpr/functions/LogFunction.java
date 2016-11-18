package com.github.basking2.sdsai.sexpr.functions;

import com.github.basking2.sdsai.sexpr.EvaluationContext;
import com.github.basking2.sdsai.sexpr.SExprRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Iterator;

import static com.github.basking2.sdsai.sexpr.util.Iterators.mapIterator;

public class LogFunction implements FunctionInterface<Object> {

    private static Logger LOG = LoggerFactory.getLogger(LogFunction.class);
    private final Method method;

    public static enum LEVEL {
        DEBUG,
        INFO,
        WARN,
        ERROR
    };

    public LogFunction(LEVEL level) {
        try {
            method = LOG.getClass().getMethod(level.toString().toLowerCase(), String.class, Object.class);
        } catch (final NoSuchMethodException e) {
            throw new SExprRuntimeException("Cannot find logging signature.", e);
        }

    }

    @Override
    public Object apply(Iterator<?> iterator, EvaluationContext evaluationContext) {
        return mapIterator(iterator, e -> {
            method.invoke(LOG, "%s", e);
            return e;
        });
    }
}
