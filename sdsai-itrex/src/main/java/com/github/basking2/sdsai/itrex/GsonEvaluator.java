package com.github.basking2.sdsai.itrex;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.concurrent.Executor;

public class GsonEvaluator extends Evaluator {

    public GsonEvaluator(final Executor executor) {
        super(executor);
    }

    public GsonEvaluator() {
        super();
    }

    public Object evaluateJson(final String json, final EvaluationContext evaluationContext) {
        final Gson gson = new Gson();

        return evaluate(gson.fromJson(json, ArrayList.class), evaluationContext);
    }
}
