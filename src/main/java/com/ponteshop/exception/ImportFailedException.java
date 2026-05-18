package com.ponteshop.exception;

public class ImportFailedException extends RuntimeException {
    public ImportFailedException(String message) {
        super(message);
    }

    public ImportFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}

