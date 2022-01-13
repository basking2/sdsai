/**
 * Copyright (c) 2013-2022 Sam Baskinger
 */
package com.github.basking2.sdsai.util;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringInterpolator {

    /**
     * Regular expression to get from the start of a string to a $ that signals the start of a variable name.
     *
     * We first must match from the beginning of the string
     * all non \ and non $ characters or we match an even
     * number of \ characters followed by a $.
     *
     * The first $ match following that sequence is the start of the name or
     * the pattern {name}.
     *
     */
    public static String startVariable = "(?:[^$\\\\]|(?:\\\\)+\\$)*";
    public static String variableNameClass = "[a-zA-Z0-9_.]+";

    static Pattern matchFirstName = Pattern.compile(
            String.format("(%s)\\$(?:(%s)|\\{(%s)\\})", startVariable, variableNameClass, variableNameClass)
    );

    public static String formatString(final String input, final Properties properties) {
        return formatString(input, name -> properties.get(name));
    }

    public static String formatString(final String input, final Map<String, ? extends Object> environment) {
        return formatString(input, name -> environment.get(name));
    }

    public static String formatString(final String input, final Function<String, ? extends Object> mapper) {
        String output = "";
        final Matcher matcher = matchFirstName.matcher(input);

        int i = 0;
        for (; matcher.find(i); i = matcher.end()) {
            final String prefix = matcher.group(1);
            final String name1 = matcher.group(2);
            final String name2 = matcher.group(3);

            if (matcher.start() > i) {
                output += input.substring(i, matcher.end());
            } else {
                final String name = name1 != null ? name1 : name2;
                final Object o = mapper.apply(name);

                if (o == null) {
                    throw new NoSuchElementException("Name " + name + " was not found.");
                }

                output += prefix + o;
            }
        }

        output += input.substring(i);

        return output;
    }
}
