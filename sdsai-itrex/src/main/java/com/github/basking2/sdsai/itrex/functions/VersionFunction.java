/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.SExprRuntimeException;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

/**
 * Igore all arguments and return the version of this package.
 */
public class VersionFunction implements FunctionInterface<String> {
    @Override
    public String apply(Iterator<?> iterator, EvaluationContext evaluationContext) {
        Properties p = new Properties();
        try {
            p.load(
                getClass().getResourceAsStream(getClass().getSimpleName() + ".properties")
            );

            return p.getProperty("version", "not available");
        } catch (IOException e) {
            throw new SExprRuntimeException("Loading version", e);
        }
    }
}
