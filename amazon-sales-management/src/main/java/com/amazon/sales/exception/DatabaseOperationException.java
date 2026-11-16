package com.amazon.sales.exception;

/**
 * Wraps low-level checked SQL exceptions and database connection failures (maps to HTTP 500).
 */
public class DatabaseOperationException extends RuntimeException {

    public DatabaseOperationException(String message) {
        super(message);
    }

    public DatabaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
