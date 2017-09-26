package com.github.basking2.sdsai.itrex;

import com.github.basking2.sdsai.itrex.iterators.Iterators;

import java.util.Iterator;

/**
 * This generator will convert objects that represent expressions into a textual form.
 *
 * If Objects are encountered that are not primitives, lists, or iterables, the will be converted to strings.
 */
public class SimpleExpressionGenerator {
    final private Object expression;
    final StringBuilder sb = new StringBuilder();

    /**
     * A prefix for pretty printing.
     */
    final String prefix;

    public SimpleExpressionGenerator(final Object expression, final String prefix) {
        this.expression = expression;
        this.prefix = prefix;
    }

    public SimpleExpressionGenerator(final Object expression) {
        this(expression, null);
    }

    public String generate() {
        emit(expression, 0);

        return sb.toString();
    }

    private void emit(final Object o, final int depth) {

        if (o == null) {
            emit("", depth);
        }
        else if (o instanceof Integer) {
            emitPrefix(depth);
            sb.append((Integer)o);
        }
        else if (o instanceof Long) {
            emitPrefix(depth);
            sb.append((Long)o).append("l");
        }
        else if (o instanceof Double) {
            emitPrefix(depth);
            sb.append((Double)o).append("d");
        }
        else if (o instanceof Float) {
            // Treat floats as doubles.
            emit(((Float)o).doubleValue(), depth);
        }
        else if (o instanceof CommentBlock) {
            emitPrefix(depth);
            sb.append("[* ").append(((CommentBlock)o).getContents()).append(" *]");
        }
        else if (o instanceof String) {
            final String s = o.toString();

            emitPrefix(depth);

            // If a word is all "safe" characters, no need to quote it.
            if (SimpleExpressionParser.WORD.matcher(s).matches()) {
                sb.append(s);
            }
            else {
                sb.append(
                        "\""
                        + s
                                .replaceAll("\\\\", "\\\\\\\\")
                                .replaceAll("\"", "\\\"")
                        + "\"");
            }
        }
        else {

            final Iterator<Object> i = Iterators.toIterator(o);

            if (i == null) {
                emit(o.toString(), depth);
            }
            else {
                emitPrefix(depth);
                sb.append("[ ");

                if(i.hasNext()) {
                    emit(i.next(), depth+1);
                    while (i.hasNext()) {
                        sb.append(", ");
                        emit(i.next(), depth+1);
                    }
                }

                emitPrefix(depth);
                sb.append(" ]");
            }
        }
    }

    private void emitPrefix(final int depth) {
        if (prefix == null || prefix.isEmpty()) {
            return;
        }

        sb.append("\n");
        for (int i = 0; i < depth; ++i) {
            sb.append(prefix);
        }
    }

    /**
     * Meta object that allows generating comment blocks.
     */
    public static class CommentBlock {
        private final String contents;
        public CommentBlock(final String contents) {
            this.contents = contents;
        }

        public String getContents() {
            return contents;
        }
    }
}
