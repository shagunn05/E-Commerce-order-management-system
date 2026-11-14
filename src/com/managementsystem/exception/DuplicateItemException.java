package com.managementsystem.exception;

public class DuplicateItemException extends ManagementException {
    public DuplicateItemException(String itemId) {
        super("ERR_DUPLICATE_ITEM", "An item with ID '" + itemId + "' already exists.");
    }
}
