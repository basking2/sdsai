package com.github.basking2.sdsai.sexpr;

import com.google.gson.Gson;

import java.util.ArrayList;

public class GsonEvaluator extends Evaluator {
    public Object evaluateJson(final String json) {
        final Gson gson = new Gson();

        return evaluate(gson.fromJson(json, ArrayList.class));
    }
}
