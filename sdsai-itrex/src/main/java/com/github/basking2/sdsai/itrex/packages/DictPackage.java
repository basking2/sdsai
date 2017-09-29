package com.github.basking2.sdsai.itrex.packages;

import com.github.basking2.sdsai.itrex.functions.DictFunction;
import com.github.basking2.sdsai.itrex.functions.FunctionInterface;

import java.util.Map;

public class DictPackage {
    public static final FunctionInterface<Map<Object, Object>> dict = new DictFunction();
    public static final FunctionInterface<Object> dictGet = (args, ctx) -> {
        final Map<Object, Object> m = (Map<Object, Object>)args.next();
        return m.get(args.next());
    };
    public static final FunctionInterface<Map<Object, Object>> dictPut = (args, ctx) -> {
        final Map<Object, Object> m = (Map<Object, Object>)args.next();
        final Object key = args.next();
        final Object val = args.next();
        m.put(key, val);
        return m;
    };
}
