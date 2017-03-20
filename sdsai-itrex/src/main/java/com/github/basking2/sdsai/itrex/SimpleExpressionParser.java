package com.github.basking2.sdsai.itrex;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A parser that is JSON-esque, but removes some commas and quotes to better suite the particulars of this language.
 *
 * The JSON
 *
 * <code>
 *     [ "map", ["curry", "f"], ["list", 1, 2, 3]]
 * </code>
 *
 * may be parsed to the same expression
 *
 * <code>
 *     [map [curry f] [list 1 2 3]]
 * </code>
 *
 * String may still be used, but we allow for unquoted tokens.
 */
public class SimpleExpressionParser {

    public static final Pattern SKIP_WS = Pattern.compile("^\\s*");
    public static final Pattern COMMA = Pattern.compile("^\\s*,");
    public static final Pattern OPEN_BRACKET = Pattern.compile("^\\s*\\[");
    public static final Pattern CLOSE_BRACKET = Pattern.compile("^\\s*\\]");
    public static final Pattern FIRST_QUOTE = Pattern.compile("^\\s*\"");
    public static final Pattern QUOTED_STRING = Pattern.compile("^\"((?:\\\\|\\\"|[^\"])*)\"");

    public static final Pattern INTEGER = Pattern.compile("^(?:-?\\d+)");
    public static final Pattern LONG = Pattern.compile("^(?:-?\\d+)[lL]");
    public static final Pattern DOUBLE = Pattern.compile("^(?:-?\\d+\\.\\d+|\\d+D|\\d+d)");

    public static final Pattern WORD = Pattern.compile("^(?:\\w+)");

    private final String expression;
    private int position;

    /**
     * Construct the parser, but do not parse.
     * @see #parse()
     * @param expression An expression to parse.
     */
    public SimpleExpressionParser(final String expression) {
        this.expression = expression;
        this.position = 0;
    }

    private static int matchOrNeg1(final String expression, final Pattern pattern, int start) {
        Matcher m = pattern.matcher(expression).region(start, expression.length());
        if (m.find()) {
            return m.group().length();
        }

        return -1;
    }

    private static int firstQuote(final String expression, final int start) {
        return matchOrNeg1(expression, FIRST_QUOTE, start);
    }

    private static int openBracket(final String expression, final int start) {
        return matchOrNeg1(expression, OPEN_BRACKET, start);
    }

    private static int closeBracket(final String expression, final int start) {
        return matchOrNeg1(expression, CLOSE_BRACKET, start);
    }

    private void skipWs() {
        final Matcher m = SKIP_WS.matcher(expression).region(position, expression.length());
        if (m.find()) {
            position += m.group().length();
        };
    }

    /**
     * Skip past whitespace and a comma. Use in list construction.
     *
     * Lists do not require a comma, but one is tolerated.
     */
    private void skipComma() {
        final Matcher m = COMMA.matcher(expression).region(position, expression.length());
        if (m.find()) {
            position += m.group().length();
        };
    }

    final Object parse() {
        skipWs();

        int i = openBracket(expression, position);

        // This is a bracket expression.
        if (i > 0) {
            // Move the position up to the opening [ and parse that list.
            position += i;
            return parseList();
        }

        // At end of expression, return null.
        if (position >= expression.length()) {
            return null;
        }

        return parseLiteral();
    }

    /**
     * When the position points after an opening [, this parses that list.
     * @return
     */
    final private List<Object> parseList() {
        final ArrayList<Object> arrayList = new ArrayList<>();

        int i;
        for (
                i = closeBracket(expression, position);
                i == -1;
                i = closeBracket(expression, position)
        ) {
            arrayList.add(parse());
            skipComma();

            if (position >= expression.length()) {
                throw new SimpleExpressionUnclosedListException();
            }
        }

        position += i;

        return arrayList;
    }


    /**
     * Parse a quoted string, an double, a long, or an unquoted string.
     * @return A Java object or null if nothing could be parsed.
     */
    final private Object parseLiteral() {

        // This is a quote!
        int i = firstQuote(expression, position);
        if (i >= 0) {
            final Matcher m = QUOTED_STRING.matcher(expression).region(position, expression.length());
            if (m.find()) {
                position += m.group().length();
                String token = m.group(1);

                // Remove escaping.
                token = token.replaceAll("\\\\(.)", "$1");

                return token;
            }
            else {
                throw new SExprRuntimeException("Unmatched \" starting at position "+position+".");
            }
        }

        final Matcher doubleMatcher = DOUBLE.matcher(expression).region(position, expression.length());
        if (doubleMatcher.find()) {
            String token = doubleMatcher.group();
            position += token.length();
            if (token.endsWith("D") || token.endsWith("d")) {
                token = token.substring(0, token.length()-1);
            }

            return Double.valueOf(token);
        }

        final Matcher longMatcher = LONG.matcher(expression).region(position, expression.length());
        if (longMatcher.find()) {
            String token = longMatcher.group();
            position += token.length();
            if (token.endsWith("L") || token.endsWith("l")) {
                token = token.substring(0, token.length()-1);
            }

            return Long.valueOf(token);
        }

        final Matcher intMatcher = INTEGER.matcher(expression).region(position, expression.length());
        if (intMatcher.find()) {
            String token = intMatcher.group();
            position += token.length();
            return Integer.valueOf(token);
        }

        final Matcher wordMatcher = WORD.matcher(expression).region(position, expression.length());
        if (wordMatcher.find()) {
            position += wordMatcher.group().length();
            return wordMatcher.group();
        }

        throw new SExprRuntimeException("Unexpected token at position "+position);
    }

    public static Object parseExpression(final String expression) {
        return new SimpleExpressionParser(expression).parse();
    }

    /**
     * Return the position in the expression string where parsing left off.
     *
     * @return the position in the expression string where parsing left off.
     */
    public int getPosition() {
        return position;
    }
}
