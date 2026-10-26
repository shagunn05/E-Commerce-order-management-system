package com.ecommerce.exception;

public class DuplicateResourceException extends EcommerceException {
    public DuplicateResourceException(String resourceName, String fieldName, Object value) {
        super("ERR_DUPLICATE_RESOURCE",
                String.format("%s with %s '%s' already exists in the system.", resourceName, fieldName, value));
    }
}