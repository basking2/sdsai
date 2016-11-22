package com.github.basking2.sdsai.itrex.functions;

/**
 * A function that has help text.
 */
public interface HelpfulFunction {
    /**
     * Returns help text for how this function should be used.
     * @param name The name the function is called. Functions may have aliases.
     * @param verbose Should this help be verbose or simplistic.
     * @return A string that describes how to use a function.
     *         This may be a Markdown formatted string.
     */
    String functionHelp(String name, boolean verbose);
}
