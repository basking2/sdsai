/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.itrex;

/**
 */
public class SExprRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 3976798317524183454L;

    public SExprRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public SExprRuntimeException(Throwable cause) {
        super(cause);
    }

    public SExprRuntimeException(String message) {
        super(message);
    }
}
