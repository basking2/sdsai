/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.itrex;

import static com.github.basking2.sdsai.itrex.iterators.Iterators.EMPTY_ITERATOR;

import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.github.basking2.sdsai.itrex.functions.*;
import com.github.basking2.sdsai.itrex.packages.*;
import com.github.basking2.sdsai.itrex.iterators.EvaluatingIterator;
import com.github.basking2.sdsai.itrex.iterators.Iterators;

/**
 */
public class Evaluator {

    private Executor executor;
    private EvaluationContext rootContext;

    /**
     * Construct a new evaluator and import the default functions.
     * 
     * @param executor An executor to use for parallelism.
     */
    public Evaluator(final Executor executor) {
        this.executor = executor;
        this.rootContext = new EvaluationContext();

        importDefaults();
    }

    /**
     * Build an evaluator, importing most of the basic packages of functions.
     */
    public Evaluator() {
        this.executor = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors());
        this.rootContext = new EvaluationContext();

        importDefaults();
    }

    /**
     * This constructor only assigns the arguments to the internal state of this instance.
     *
     * This does no initialization. If you want an empty {@link Evaluator}, use this constructor.
     *
     * @see #importDefaults()
     *
     * @param executor How concurrency is managed.
     * @param rootContext The root context.
     */
    public Evaluator(final Executor executor, final EvaluationContext rootContext) {
        this.executor = executor;
        this.rootContext = rootContext;
    }
    
    /**
     * Import the base functions, help, version and import.
     */
    public void importBase() {
        register("help", new HelpFunction());
        register("import", new ImportFunction(this, rootContext));
        register("evalItrml", new EvalItrmlFunction(this));
        register("version", new VersionFunction());
        register("nop", new NopFunction());

        // Import base.
        evaluate(new String[]{"import", BasePackage.class.getCanonicalName()});

        // Import ITRML.
        evaluate(new String[]{"evalItrml", "/base.itrml"}, getRootEvaluationContext());
    }

    /**
     * Used by constructors, this imports the default libraries.
     */
    public void importDefaults() {
        importBase();

        register("log.debug", new LogFunction(LogFunction.LEVEL.DEBUG));
        register("log.info", new LogFunction(LogFunction.LEVEL.INFO));
        register("log.warn", new LogFunction(LogFunction.LEVEL.WARN));
        register("log.error", new LogFunction(LogFunction.LEVEL.ERROR));

        // Import the string package.
        evaluate(new String[]{"import", StringPackage.class.getCanonicalName()});

        // Register toInt, toLong, toFloat, toString, toDouble.
        evaluate(new String[]{"import", CastingPackage.class.getCanonicalName()});

        // Dict.
        evaluate(new String[]{"import", DictPackage.class.getCanonicalName()});

        // And or not eq...
        evaluate(new String[]{"import", BooleanPackage.class.getCanonicalName()});

        // Map, fold, etc...
        evaluate(new String[]{"import", FunctionalPackage.class.getCanonicalName()});

        // Iterator functions.
        evaluate(new String[]{"import", IteratorPackage.class.getCanonicalName()});

        register("thread", new ThreadFunction(executor));
        register("join", new JoinFunction());

    }

    public void register(final Object name, final FunctionInterface<? extends Object> operator) {
        rootContext.register(name, operator);
    }

    /**
     * Create a default, empty, evaluation context and call {@link #evaluate(Object, EvaluationContext)}.
     *
     * @param o The object to evaluate.
     * @return The result of the evaluation.
     */
    public Object evaluate(final Object o) {
        return evaluate(o, new EvaluationContext(rootContext, Iterators.EMPTY_ITERATOR));
    }

    @SuppressWarnings("unchecked")
    public Object evaluate(final Object o, final EvaluationContext context) {
        if (o instanceof EvaluatingIterator) {
            return evaluate((EvaluatingIterator<Object>) o);
        }

        if (o instanceof Iterator) {
            return evaluate(wrap((Iterator<Object>) o, context));
        }

        if (o instanceof Iterable) {
            return evaluate(wrap(((Iterable<Object>) o).iterator(), context));
        }

        if (o instanceof Object[]) {
            return evaluate(wrap(Iterators.wrap((Object[])o), context));
        }

        return o;
    }

    public Object evaluate(final EvaluatingIterator<Object> i) {
        if (!i.hasNext()) {
            return EMPTY_ITERATOR;
        }

        final EvaluationContext context = i.getEvaluationContext();
        final Object operatorObject = i.next();
        final FunctionInterface<? extends Object> operator;

        if (operatorObject instanceof FunctionInterface) {
            // Temporary variable just to apply the SuppressWarnings annotation to.
            @SuppressWarnings("unchecked")
            final FunctionInterface<? extends Object> tmpo = (FunctionInterface<? extends Object>)operatorObject;

            operator = tmpo;
        }
        else {
            operator = context.getFunction(operatorObject);
            if (operator == null) {
                throw new SExprRuntimeException("No function " + operatorObject.toString());
            }
        }

        return operator.apply(i, context);
    }

    private EvaluatingIterator<Object> wrap(final Iterator<Object> iterator, final EvaluationContext context) {
        return new EvaluatingIterator<>(this, context, iterator);
    }

    public EvaluationContext getRootEvaluationContext() {
        return rootContext;
    }

    public EvaluationContext getChildEvaluationContext() {
        return new EvaluationContext(rootContext, Iterators.EMPTY_ITERATOR);
    }
}
