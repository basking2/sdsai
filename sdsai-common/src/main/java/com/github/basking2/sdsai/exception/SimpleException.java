package com.github.basking2.sdsai.exception;

/**
 * An exception that does not record the stack trace.
 */
public class SimpleException extends Exception {
        public SimpleException(final String msg, final Throwable cause) {
                super(msg, cause, true, false);
        }

        public SimpleException(final String msg) {
                super(msg, null, true, false);
        }

        public SimpleException(final Throwable cause) {
                super(cause.getMessage(), cause, true, false);
        }
}
