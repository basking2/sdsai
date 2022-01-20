/**
 * Copyright (c) 2013-2022 Sam Baskinger
 */
package com.github.basking2.sdsai.util;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.function.Function;

public class StringInterpolator {

    public static String formatString(final String input, final Properties properties) {
        return formatString(input, name -> properties.get(name));
    }

    public static String formatString(final String input, final Map<String, ? extends Object> environment) {
        return formatString(input, name -> environment.get(name));
    }

    /**
     *
     * @param src
     * @param starti
     * @return A 3-tuple int array of {-1, -1, -1} on error or {wordstart, wordend, nextchar-1}.
     */
    private static int[] findName(final char[] src, int starti) {
        final boolean hasBraces;

        // Skip '$'.
        starti++;

        if (starti + 2 < src.length && src[starti] == '{') {
            // {c} format.
            starti += 1;
            hasBraces = true;
        } else {
            hasBraces = false;
        }

        int i = starti + 1;
        for (; i < src.length; i++) {
            if (
                    ('a' > src[i] || 'z' < src[i])
                            && ('A' > src[i] || 'Z' < src[i])
                            && ('0' > src[i] || '9' < src[i])
                            && '.' != src[i]
                            && '_' != src[i]
            ) {
                if (hasBraces) {
                    if (i+1 >= src.length) {
                        return new int[]{-1, -1, -1};
                    } else {
                        return new int[]{starti, i, i+1};
                    }
                } else {
                    return new int[]{starti, i, i};
                }
            }
        }

        if (hasBraces) {
            return new int[]{-1, -1, -1};
        } else {
            return new int[]{starti, i, i};
        }
    }

    public static String formatString(final String input, final Function<String, ? extends Object> mapper) {
        final char[] src = input.toCharArray();
        String dst = "";

        boolean escaped = false;
        int i = 0;
        for (; i < src.length; i++) {
            switch (src[i]) {
                case '\\':
                    if (escaped) {
                        // Escaped slashes get appended.
                        dst += src[i];
                    }
                    escaped = !escaped;
                    break;
                case '$':
                    if (escaped) {
                        // Escaped $ get appended.
                        dst += '$';
                        escaped = false;
                    } else {
                        final int[] is = findName(src, i);
                        if (is[0] != -1) {
                            final String name = new String(src, is[0], is[1] - is[0]);
                            final Object value = mapper.apply(name);
                            if (value == null) {
                                throw new NoSuchElementException("Name " + name + " was not found.");
                            }
                            dst += value.toString();
                            i = is[2]-1;
                        }
                    }
                    break;
                default:
                    escaped = false;
                    dst += src[i];
            }
        }

        dst += new String(src, i, src.length-i);

        return dst;
    }
}
