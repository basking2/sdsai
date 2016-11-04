package com.github.basking2.sdsai.sexpr;

/**
 */
public class SExprRuntimeException extends RuntimeException {
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
