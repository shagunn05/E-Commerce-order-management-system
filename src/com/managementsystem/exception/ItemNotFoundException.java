package com.managementsystem.exception;

public class ItemNotFoundException extends ManagementException {
    public ItemNotFoundException(String itemId) {
        super("ERR_ITEM_NOT_FOUND", "Item with ID '" + itemId + "' was not found in the catalog/inventory.");
    }
}
