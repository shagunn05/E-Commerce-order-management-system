package com.managementsystem.exception;

public class InvalidOperationException extends ManagementException {
    public InvalidOperationException(String message) {
        super("ERR_INVALID_OP", message);
    }
}
