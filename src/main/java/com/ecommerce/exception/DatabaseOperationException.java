package com.ecommerce.exception;

/**
 * Wraps checked java.sql.SQLException thrown during raw JDBC operations,
 * preserving cause stacktrace while providing clean domain error messaging.
 */
public class DatabaseOperationException extends EcommerceException {

    public DatabaseOperationException(String operation, Throwable cause) {
        super("ERR_DATABASE_FAILURE", "Failed to execute database operation: " + operation, cause);
    }

    public DatabaseOperationException(String message) {
        super("ERR_DATABASE_FAILURE", message);
    }
}
