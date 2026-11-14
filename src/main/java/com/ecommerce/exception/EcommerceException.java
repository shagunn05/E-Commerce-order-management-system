package com.ecommerce.exception;

/**
 * Root domain exception for the E-Commerce Order Management System.
 * Extends RuntimeException to support unchecked business exception propagation.
 */
public class EcommerceException extends RuntimeException {

    private final String errorCode;

    public EcommerceException(String message) {
        super(message);
        this.errorCode = "ECOMM_GENERAL_ERROR";
    }

    public EcommerceException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public EcommerceException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
