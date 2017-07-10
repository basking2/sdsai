package com.github.basking2.sdsai.itrex.util;

/**
 * Standard ways to convert between types.
 */
public class TypeConversion {
    public static String toString(final Object o) {
        return o.toString();
    }

    public static int toInt(final Object o) {
        if (o instanceof Double) {
            return ((Double) o).intValue();
        } else if (o instanceof Float) {
            return ((Float) o).intValue();
        } else if (o instanceof Long) {
            return ((Long) o).intValue();
        } else if (o instanceof Integer) {
            return ((Integer) o);
        } else if (o instanceof String) {
            return Integer.valueOf((String) o);
        } else {
            throw new IllegalArgumentException("Cannot cast " + o.getClass().getName() + " to double.");
        }
    }

    public static float toFloat(final Object o) {
        if (o instanceof Double) {
            return ((Double) o).floatValue();
        } else if (o instanceof Float) {
            return ((Float) o);
        } else if (o instanceof Long) {
            return ((Long) o).floatValue();
        } else if (o instanceof Integer) {
            return ((Integer) o).floatValue();
        } else if (o instanceof String) {
            return Float.valueOf((String) o);
        } else {
            throw new IllegalArgumentException("Cannot cast " + o.getClass().getName() + " to double.");
        }
    }

    public static long toLong(final Object o) {
        if (o instanceof Double) {
            return ((Double) o).longValue();
        } else if (o instanceof Float) {
            return ((Float) o).longValue();
        } else if (o instanceof Long) {
            return ((Long) o);
        } else if (o instanceof Integer) {
            return ((Integer) o).longValue();
        } else if (o instanceof String) {
            return Long.valueOf((String) o);
        } else {
            throw new IllegalArgumentException("Cannot cast " + o.getClass().getName() + " to double.");
        }
    }

    public static double toDouble(final Object o) {
        if (o instanceof Double) {
            return (Double) o;
        } else if (o instanceof Float) {
            return ((Float) o).doubleValue();
        } else if (o instanceof Long) {
            return ((Long) o).doubleValue();
        } else if (o instanceof Integer) {
            return ((Integer) o).doubleValue();
        } else if (o instanceof String) {
            return Double.valueOf((String) o);
        } else {
            throw new IllegalArgumentException("Cannot cast " + o.getClass().getName() + " to double.");
        }
    }

    /**
     * Convert the parameter o to a boolean using its truthy or falsey ness.
     *
     * <ol>
     *     <li>Null is false.</li>
     *     <li>The strings "false", "0", "no", "f", and "off" all result in a false value.
     *     <li>Everything not listed above results in true.</li>
     * </ol>
     * @param o The object to convert to a boolean.
     * @return The boolean value of {@code o}.
     */
    public static boolean toBoolean(final Object o) {
        if (o == null) {
            return false;
        }

        if (o instanceof Boolean) {
            return (Boolean) o;
        }

        for (final String falsy : new String[]{ "false", "0", "no", "f", "off"}) {
            if (falsy.equalsIgnoreCase(o.toString())) {
                return false;
            }
        }

        return true;
    }
}
