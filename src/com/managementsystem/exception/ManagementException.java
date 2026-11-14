package com.managementsystem.exception;

/**
 * Base exception for all domain-specific errors in the Management System.
 */
public class ManagementException extends Exception {
    private final String errorCode;

    public ManagementException(String message) {
        super(message);
        this.errorCode = "ERR_GENERAL";
    }

    public ManagementException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ManagementException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "[" + errorCode + "] " + getMessage();
    }
}
