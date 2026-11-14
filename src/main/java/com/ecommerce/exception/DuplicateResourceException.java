package com.ecommerce.exception;

/**
 * Thrown when attempting to insert an entity with an existing unique identifier (e.g., SKU, email).
 */
public class DuplicateResourceException extends EcommerceException {

    public DuplicateResourceException(String resourceName, String fieldName, Object value) {
        super("ERR_DUPLICATE_RESOURCE",
                String.format("%s with %s '%s' already exists in the system.", resourceName, fieldName, value));
    }
}
