package com.ecommerce.exception;

public class InsufficientStockException extends EcommerceException {
    public InsufficientStockException(String sku, int requested, int available) {
        super("ERR_INSUFFICIENT_STOCK",
                String.format("Insufficient stock for SKU '%s'. Requested: %d, Available: %d.", sku, requested, available));
    }
}