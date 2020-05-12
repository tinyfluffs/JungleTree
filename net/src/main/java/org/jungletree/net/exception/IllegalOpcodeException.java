package org.jungletree.net.exception;

public class IllegalOpcodeException extends RuntimeException {

    public IllegalOpcodeException() {
        super();
    }

    public IllegalOpcodeException(String message) {
        super(message);
    }

    public IllegalOpcodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalOpcodeException(Throwable cause) {
        super(cause);
    }

    protected IllegalOpcodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
