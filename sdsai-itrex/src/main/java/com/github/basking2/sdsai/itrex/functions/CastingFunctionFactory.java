package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.Evaluator;

/**
 * Register casting functions in a given {@link Evaluator}.
 * 
 * <ul>
 * <li>[toString]</li>
 * <li>[toInt]</li>
 * <li>[toFloat]</li>
 * <li>[toLong]</li>
 * <li>[toDouble]</li>
 * </ul>
 */
public class CastingFunctionFactory {
    
    public static void register(final Evaluator evaluator) {
       evaluator.register("toString", (itr, ctx) -> castToString(itr.next())); 
       evaluator.register("toInt", (itr, ctx) -> castToInt(itr.next())); 
       evaluator.register("toFloat", (itr, ctx) -> castToFloat(itr.next())); 
       evaluator.register("toLong", (itr, ctx) -> castToLong(itr.next())); 
       evaluator.register("toDouble", (itr, ctx) -> castToDouble(itr.next())); 
    }

    public static String castToString(Object o) {
        return o.toString();
    }

    public static Float castToFloat(Object o) {
        if (o instanceof Double) {
            return ((Double)o).floatValue();
        }
        else if (o instanceof Float) {
            return ((Float)o);
        }
        else if (o instanceof Long) {
            return ((Long)o).floatValue();
        }
        else if (o instanceof Integer) {
            return ((Integer)o).floatValue();
        }
        else if (o instanceof String) {
            return Float.valueOf((String)o);
        }
        else {
            throw new IllegalArgumentException("Cannot cast "+o.getClass().getName()+" to double.");
        }
    }

    public static Integer castToInt(Object o) {
        if (o instanceof Double) {
            return ((Double)o).intValue();
        }
        else if (o instanceof Float) {
            return ((Float)o).intValue();
        }
        else if (o instanceof Long) {
            return ((Long)o).intValue();
        }
        else if (o instanceof Integer) {
            return ((Integer)o);
        }
        else if (o instanceof String) {
            return Integer.valueOf((String)o);
        }
        else {
            throw new IllegalArgumentException("Cannot cast "+o.getClass().getName()+" to double.");
        }
    }

    public static Long castToLong(Object o) {
        if (o instanceof Double) {
            return ((Double)o).longValue();
        }
        else if (o instanceof Float) {
            return ((Float)o).longValue();
        }
        else if (o instanceof Long) {
            return ((Long)o);
        }
        else if (o instanceof Integer) {
            return ((Integer)o).longValue();
        }
        else if (o instanceof String) {
            return Long.valueOf((String)o);
        }
        else {
            throw new IllegalArgumentException("Cannot cast "+o.getClass().getName()+" to double.");
        }
    }

    public static Double castToDouble(final Object o) {
        if (o instanceof Double) {
            return (Double)o;
        }
        else if (o instanceof Float) {
            return ((Float)o).doubleValue();
        }
        else if (o instanceof Long) {
            return ((Long)o).doubleValue();
        }
        else if (o instanceof Integer) {
            return ((Integer)o).doubleValue();
        }
        else if (o instanceof String) {
            return Double.valueOf((String)o);
        }
        else {
            throw new IllegalArgumentException("Cannot cast "+o.getClass().getName()+" to double.");
        }
    }
}
