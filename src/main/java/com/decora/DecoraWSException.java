package com.decora;

public class DecoraWSException extends Exception {

    public DecoraWSException(final String message) {
        super(message);
    }

    public DecoraWSException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
