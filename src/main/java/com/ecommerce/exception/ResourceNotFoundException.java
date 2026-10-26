package com.ecommerce.exception;

public class ResourceNotFoundException extends EcommerceException {


    public ResourceNotFoundException(String resourceName, Object identifier) {
        super("ERR_RESOURCE_NOT_FOUND", String.format("%s with identifier '%s' was not found.", resourceName, identifier));
    }


    public ResourceNotFoundException(String message) {
        super("ERR_RESOURCE_NOT_FOUND", message);
    }
}