package org.jungletree.net.exception;

public class ChannelClosedException extends RuntimeException {

    public ChannelClosedException() {
        super();
    }

    public ChannelClosedException(String message) {
        super(message);
    }

    public ChannelClosedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChannelClosedException(Throwable cause) {
        super(cause);
    }

    protected ChannelClosedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
