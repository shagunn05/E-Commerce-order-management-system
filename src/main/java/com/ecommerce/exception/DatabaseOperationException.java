package com.ecommerce.exception;

public class DatabaseOperationException extends EcommerceException {
    public DatabaseOperationException(String operation, Throwable cause) {
        super("ERR_DATABASE_FAILURE", "Failed to execute database operation: " + operation, cause);
    }

    public DatabaseOperationException(String message) {
        super("ERR_DATABASE_FAILURE", message);
    }
}