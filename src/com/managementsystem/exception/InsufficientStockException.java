package com.managementsystem.exception;

public class InsufficientStockException extends ManagementException {
    public InsufficientStockException(String itemId, int requested, int available) {
        super("ERR_INSUFFICIENT_STOCK", "Insufficient stock for item '" + itemId + "'. Requested: " + requested + ", Available: " + available);
    }
}
