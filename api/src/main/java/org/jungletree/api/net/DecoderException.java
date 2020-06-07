package org.jungletree.api.net;

public class DecoderException extends RuntimeException {

    public DecoderException() {
    }

    public DecoderException(String message) {
        super(message);
    }

    public DecoderException(String message, Throwable cause) {
        super(message, cause);
    }

    public DecoderException(Throwable cause) {
        super(cause);
    }

    public DecoderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
