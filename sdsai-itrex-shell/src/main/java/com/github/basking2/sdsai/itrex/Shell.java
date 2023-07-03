/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.itrex;

import org.apache.commons.cli.*;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.history.DefaultHistory;
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

    private static final String EXIT_OBJECT = "exiting...";

    public static void main(final String[] argv) throws IOException {
        final CommandLineParser parser = new DefaultParser();
        final CommandLine commandLine;

        try {
            commandLine = parser.parse(buildOptions(), argv, false);
        }
        catch (ParseException e) {
            System.err.println(e.getMessage());
            return;
        }

        if (commandLine.hasOption("help")) {
            final HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("itrex [options]", buildOptions());
            return;
        }

        final Evaluator evaluator;

        try {
            evaluator = buildEvaluator(commandLine);
        }
        catch (final Throwable e) {
            System.err.println(e.getMessage());
            return;
        }

        // If the user wants to evaluate the command line or a file, we do not go interactive.
        if (commandLine.hasOption("evaluate") || commandLine.hasOption("file")) {
            if (commandLine.hasOption("file")) {
                for (final String include : commandLine.getOptionValues("file")) {
                    evaluator.evaluate(new Object[]{"evalItrml", include}, evaluator.getRootEvaluationContext());
                }
            }

            if (commandLine.hasOption("evaluate")) {
                for (final String include : commandLine.getOptionValues("evaluate")) {
                    final Object expression = new SimpleExpressionParser(include).parse();
                    evaluator.evaluate(expression, evaluator.getRootEvaluationContext());
                }
            }
        }
        else {

            final EvaluationContext context = evaluator.getChildEvaluationContext();

            boolean run = true;

            final BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

            final Terminal terminal = TerminalBuilder.builder()
                    .name("Itrex")
                    .system(true)
                    .build();

            final LineReader lineReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .history(new DefaultHistory())
                    //.completer(new MyCompleter())
                    //.highlighter(new MyHighlighter())
                    //.parser(new MyParser())
                    .build();

            while (run) {
                try {
                    System.out.flush();

                    final String expStr = collectExpression(lineReader);

                    final Object exp = new SimpleExpressionParser(expStr).parse();

                    final Object result = evaluator.evaluate(exp, context);

                    if (result == EXIT_OBJECT) {
                        run = false;
                    }

                    System.out.println(result.toString());
                } catch (final EndOfFileException t) {
                    run = false;
                    System.out.println("End of input.");
                } catch (final Throwable t) {
                    // Print exception to stdout. This prevents it interleaving w/ the prompt.
                    System.out.println(t.getMessage());
                }
            }
        }
    }

    public static String collectExpression(final LineReader lineReader) {
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

    public static Evaluator buildEvaluator(final CommandLine commandLine) {
        final Evaluator evaluator = new Evaluator(Executors.newWorkStealingPool());

        // Include files.
        if (commandLine.hasOption("include")) {
            for (final String include : commandLine.getOptionValues("include")) {
                evaluator.evaluate( new Object[]{"evalItrml", include}, evaluator.getRootEvaluationContext());
            }
        }

        evaluator.register("exit", (iterator, evaluationContext) -> EXIT_OBJECT);

        return evaluator;
    }

    private static Options buildOptions() {
        final Options opts = new Options();

        opts.addOption("i", "include", true, "A file to include before starting evaluation.");
        opts.addOption("e", "evaluate", true, "Evaluate a segment of text as ITRML.");
        opts.addOption("f", "file", true, "A file to evaluate and then exit.");
        opts.addOption("h", "help", false, "Help.");

        return opts;

    }
}
