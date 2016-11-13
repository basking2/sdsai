package com.github.basking2.sdsai.sexpr.functions;

/**
 * A function that has help text.
 */
public interface HelpfulFunction {
    /**
     * Returns help text for how this function should be used.
     * @param verbose Should this help be verbose or simplistic.
     * @return A string that describes how to use a function.
     *         This may be a Markdown formatted string.
     */
    String functionHelp(boolean verbose);
}
