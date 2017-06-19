package com.github.basking2.sdsai.itrex.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Some utility functions for parsing arguments out of an iterator.
 *
 * This is useful in collecting arguments to functions.
 */
public class Arguments {
    /**
     * Consume all argument values and parse them into a Map.
     *
     * This will {@code split(args, 2)} all arguments to form a name-value pair. The
     * value is put into the hash that is returned.
     *
     * @param args The iterator of name-value pairs.
     * @param separator The string that separates names (the left) from the values (the right).
     * @return A map of all names to values parsed out.
     */
    public static Map<String, String> parseArgs(final Iterator<?> args, final String separator) {
        final HashMap<String, String> parsedArgs = new HashMap<>();

        while (args.hasNext()) {
            final String[] namedArg = args.next().toString().split(separator, 2);
            if (namedArg.length == 2) {
                parsedArgs.put(namedArg[0], namedArg[1]);
            }
        }

        return parsedArgs;
    }
}
