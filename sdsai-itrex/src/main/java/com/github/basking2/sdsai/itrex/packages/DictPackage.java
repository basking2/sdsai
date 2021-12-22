/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.itrex.packages;

import com.github.basking2.sdsai.itrex.functions.DictFunction;
import com.github.basking2.sdsai.itrex.functions.FunctionInterface;

import java.util.Map;

public class DictPackage {

    public static final String __package = "dict";

    public static final FunctionInterface<Map<Object, Object>> mk = new DictFunction();
    public static final FunctionInterface<Object> get = (args, ctx) -> {
        @SuppressWarnings("unchecked")
        final Map<Object, Object> m = (Map<Object, Object>)args.next();

        final Object key = args.next();

        if (m.containsKey(key)) {
            return m.get(key);
        }
        else if (args.hasNext()) {
            return args.next();
        }
        else {
            return null;
        }
    };
    public static final FunctionInterface<Map<Object, Object>> put = (args, ctx) -> {
        @SuppressWarnings("unchecked")
        final Map<Object, Object> m = (Map<Object, Object>)args.next();
        final Object key = args.next();
        final Object val = args.next();
        m.put(key, val);
        return m;
    };
}
