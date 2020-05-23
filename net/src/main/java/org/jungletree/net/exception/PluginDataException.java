package org.jungletree.net.exception;

public class PluginDataException extends RuntimeException {

    public PluginDataException() {
    }

    public PluginDataException(String message) {
        super(message);
    }

    public PluginDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public PluginDataException(Throwable cause) {
        super(cause);
    }

    public PluginDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
