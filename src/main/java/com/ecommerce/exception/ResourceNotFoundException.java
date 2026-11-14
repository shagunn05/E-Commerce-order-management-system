package com.ecommerce.exception;

/**
 * Thrown when an entity (Product, Order, Customer) is not found in the persistence layer.
 */
public class ResourceNotFoundException extends EcommerceException {

    public ResourceNotFoundException(String resourceName, Object identifier) {
        super("ERR_RESOURCE_NOT_FOUND", String.format("%s with identifier '%s' was not found.", resourceName, identifier));
    }
}
