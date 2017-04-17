package com.github.basking2.sdsai.itrex;

import org.jline.reader.*;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;

/**
 * A simple interactive shell to interact with ItrEx.
 */
public class Shell {

    private static String EXIT_OBJECT = "exiting...";

    public static final void main(final String[] argv) throws IOException {
        Evaluator evaluator = buildEvaluator();

        boolean run = true;

        final BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        final Terminal terminal = TerminalBuilder.builder()
                .name("Itrex")
                .system(false)
                .build();

        final LineReader lineReader = LineReaderBuilder.builder()
                .terminal(terminal)
                //.completer(new MyCompleter())
                //.highlighter(new MyHighlighter())
                //.parser(new MyParser())
                .build();

        while (run) {
            try {
                System.out.flush();

                final String expStr = collectExpression(lineReader);

                final Object exp = new SimpleExpressionParser(expStr).parse();

                final Object result = evaluator.evaluate(exp);

                if (result == EXIT_OBJECT) {
                    run = false;
                }

                System.out.println(result.toString());
            }
            catch (final Throwable t) {
                // Print exception to stdout. This prevents it interleaving w/ the prompt.
                System.out.println(t.getMessage());
                //t.printStackTrace(System.out);
            }

        }
    }

    public static final String collectExpression(final LineReader lineReader) throws IOException {
        final StringBuilder sb = new StringBuilder();

        boolean parsed = false;

        while (! parsed) {
            final String line = lineReader.readLine("itrex> ");

            if (line == null) {
                return "[exit]";
            }

            sb.append(line);

            final String expr = sb.toString().trim();

            try {
                final SimpleExpressionParser sep = new SimpleExpressionParser(expr);

                sep.parse();

                // System.out.printf("[[[[Checking %d vs %d: %s]]]]", sep.getPosition(), expr.length(), expr);
                if (sep.getPosition() >= expr.length()) {
                    parsed = true;
                }
            }
            catch (final SimpleExpressionUnclosedListException e) {
                // Nop - let parsing continue.
            }
        }

        return sb.toString();
    }

    public static Evaluator buildEvaluator() {
        Evaluator evaluator = new Evaluator(Executors.newWorkStealingPool());

        evaluator.register("exit", (iterator, evaluationContext) -> EXIT_OBJECT);

        return evaluator;
    }
}
