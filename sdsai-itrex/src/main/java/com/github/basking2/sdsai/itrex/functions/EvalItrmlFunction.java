/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.Evaluator;
import com.github.basking2.sdsai.itrex.SExprRuntimeException;
import com.github.basking2.sdsai.itrex.SimpleExpressionParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Iterator;

public class EvalItrmlFunction implements FunctionInterface<Object> {

    public final Evaluator evaluator;

    public EvalItrmlFunction(final Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public Object apply(Iterator<?> iterator, EvaluationContext evaluationContext) {
        final String file = iterator.next().toString();

        try {
            final Object expression = tryLoadItrml(file);
            return evaluator.evaluate(expression, evaluationContext);
        }
        catch (final IOException e) {
            throw new SExprRuntimeException("Loading "+file, e);
        }
    }

    private Object tryLoadItrml(final String file) throws IOException {
        InputStream is = getClass().getResourceAsStream(file);

        if (is == null) {
            final File f = new File(file);
            if (!f.exists()) {
                throw new SExprRuntimeException("File not found: " + file);
            }

            if (!f.canRead()) {
                throw new SExprRuntimeException("File not readable: " + file);
            }

            is = new FileInputStream(f);
        }

        final SimpleExpressionParser sp = new SimpleExpressionParser(toString(is));

        return sp.parse();
    }

    private String toString(final InputStream is) throws IOException {
        final byte[] buffer = new byte[1024];
        final StringBuilder sb = new StringBuilder();
        for (
            int offset = is.read(buffer);
            offset >= 0;
            offset = is.read(buffer)
        ) {
            sb.append(new String(buffer, 0, offset, Charset.forName("UTF-8")));
        }

        return sb.toString();
    }

}
